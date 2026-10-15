package com.petadopt.service;

import com.petadopt.dao.ApplicationDAO;
import com.petadopt.dao.ApplicationDAOImpl;
import com.petadopt.dao.MessageDAO;
import com.petadopt.dao.MessageDAOImpl;
import com.petadopt.dao.PetDAO;
import com.petadopt.dao.PetDAOImpl;
import com.petadopt.exception.AccessDeniedException;
import com.petadopt.exception.DataAccessException;
import com.petadopt.exception.ResourceNotFoundException;
import com.petadopt.exception.ValidationException;
import com.petadopt.model.Application;
import com.petadopt.model.ApplicationStatus;
import com.petadopt.model.Message;
import com.petadopt.model.Pet;
import com.petadopt.util.DBConnection;
import com.petadopt.util.Validator;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdoptionService {
    public static final List<String> HOUSING_TYPES =
            List.of("Apartment", "Independent house", "Farmhouse", "Shared accommodation");

    private final ApplicationDAO applicationDAO = new ApplicationDAOImpl();
    private final PetDAO petDAO = new PetDAOImpl();
    private final MessageDAO messageDAO = new MessageDAOImpl();
    private final SettingService settings = SettingService.getInstance();

    // ------------------------------------------------------------ adopter side
    public long apply(long adopterId, long petId, String reason, String housing, String hasOtherPets,
            String experience) {
        new Validator()
                .required(reason, "Reason for adopting").maxLength(reason, 1000, "Reason for adopting")
                .check(HOUSING_TYPES.contains(Validator.clean(housing)), "Please choose your housing type.")
                .maxLength(experience, 255, "Experience")
                .throwIfInvalid();

        Pet pet = petDAO.findById(petId).orElseThrow(() -> new ResourceNotFoundException("Pet #" + petId));
        if (!pet.isApproved() || !pet.isAvailable()) {
            throw new ValidationException(pet.getName() + " is no longer available for adoption.");
        }
        if (applicationDAO.exists(petId, adopterId)) {
            throw new ValidationException("You have already applied to adopt " + pet.getName() + ".");
        }
        int limit = settings.getInt(SettingService.MAX_ACTIVE_APPLICATIONS);
        if (applicationDAO.countPendingByAdopter(adopterId) >= limit) {
            throw new ValidationException("You can have at most " + limit
                    + " pending applications at a time. Wait for a decision before applying again.");
        }

        Application application = new Application();
        application.setPetId(petId);
        application.setAdopterId(adopterId);
        application.setReason(reason.trim());
        application.setHousingType(housing.trim());
        application.setHasOtherPets("yes".equalsIgnoreCase(Validator.clean(hasOtherPets)));
        application.setExperience(Validator.clean(experience));
        try {
            return applicationDAO.create(application);
        } catch (DataAccessException e) {
            if (e.isDuplicateKey()) { // two quick double-clicks: the unique key is the final safety net
                throw new ValidationException("You have already applied to adopt " + pet.getName() + ".");
            }
            throw e;
        }
    }

    public List<Application> listForAdopter(long adopterId) {
        return applicationDAO.findByAdopter(adopterId);
    }

    /** Status lookup by application ID; an adopter can only see their own applications. */
    public Application track(long adopterId, long applicationId) {
        Application app = applicationDAO.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application #" + applicationId));
        if (app.getAdopterId() != adopterId) {
            throw new ResourceNotFoundException("Application #" + applicationId);
        }
        return app;
    }

    public boolean hasApplied(long adopterId, long petId) {
        return applicationDAO.exists(petId, adopterId);
    }

    /** Splits an adopter's applications into {true = adopted pets, false = everything else}. */
    public Map<Boolean, List<Application>> splitAdoptedAndOther(List<Application> applications) {
        return applications.stream().collect(Collectors.partitioningBy(Application::isApproved));
    }

    // ------------------------------------------------------------ shelter side
    public List<Application> listForShelter(long shelterId, String statusFilter) {
        ApplicationStatus status = null;
        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                status = ApplicationStatus.valueOf(statusFilter.trim());
            } catch (IllegalArgumentException ignored) {
                status = null;
            }
        }
        return applicationDAO.findByShelter(shelterId, status);
    }

    /**
     * Approves or rejects an application inside ONE database transaction.
     * Approval also marks the pet adopted and auto-rejects the competing applications; if any step
     * fails everything is rolled back so the data can never end up half-updated.
     */
    public Application decide(long shelterId, long applicationId, boolean approve, String note) {
        new Validator().maxLength(note, 500, "Note").throwIfInvalid();
        Connection tx = null;
        Application app;
        try {
            tx = DBConnection.getInstance().getConnection();
            tx.setAutoCommit(false);

            app = applicationDAO.findByIdForUpdate(tx, applicationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Application #" + applicationId));
            if (app.getShelterId() != shelterId) {
                throw new AccessDeniedException();
            }
            if (!app.isPending()) {
                throw new ValidationException("This application was already " + app.getStatus().name().toLowerCase()
                        + ".");
            }
            if (approve) {
                if (petDAO.markAdopted(tx, app.getPetId()) == 0) {
                    throw new ValidationException(app.getPetName() + " has already been adopted.");
                }
                applicationDAO.updateStatus(tx, applicationId, ApplicationStatus.APPROVED, note);
                applicationDAO.rejectOthers(tx, app.getPetId(), applicationId,
                        "Another application was approved for this pet.");
            } else {
                applicationDAO.updateStatus(tx, applicationId, ApplicationStatus.REJECTED, note);
            }
            tx.commit();
        } catch (SQLException e) {
            rollbackQuietly(tx);
            throw new DataAccessException(e);
        } catch (RuntimeException e) {
            rollbackQuietly(tx);
            throw e;
        } finally {
            closeQuietly(tx);
        }
        notifyAdopter(app, approve, note);
        return app;
    }

    /** Best-effort in-app notification; a failure here must not undo the decision. */
    private void notifyAdopter(Application app, boolean approved, String note) {
        try {
            Message m = new Message();
            m.setSenderId(app.getShelterId());
            m.setReceiverId(app.getAdopterId());
            String text = "Your application #" + app.getId() + " for " + app.getPetName() + " was "
                    + (approved ? "approved. Congratulations!" : "not approved this time.");
            if (note != null && !note.isBlank()) {
                text += " Note from the shelter: " + note.trim();
            }
            m.setContent(text);
            messageDAO.send(m);
        } catch (RuntimeException ignored) {
            // notification is optional
        }
    }

    private static void rollbackQuietly(Connection c) {
        if (c != null) {
            try {
                c.rollback();
            } catch (SQLException ignored) {
                // nothing more we can do
            }
        }
    }

    private static void closeQuietly(Connection c) {
        if (c != null) {
            try {
                c.setAutoCommit(true);
                c.close();
            } catch (SQLException ignored) {
                // connection is discarded anyway
            }
        }
    }
}
