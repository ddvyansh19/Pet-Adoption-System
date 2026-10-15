package com.petadopt.dao;

import com.petadopt.model.Application;
import com.petadopt.model.ApplicationStatus;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ApplicationDAO {
    long create(Application application);

    Optional<Application> findById(long id);

    List<Application> findByAdopter(long adopterId);

    /** @param status optional filter; null returns all applications for the shelter's pets */
    List<Application> findByShelter(long shelterId, ApplicationStatus status);

    boolean exists(long petId, long adopterId);

    /** Number of this adopter's applications that are still waiting for a decision. */
    long countPendingByAdopter(long adopterId);

    // ---- transaction-aware operations (used by AdoptionService.approve) ----
    Optional<Application> findByIdForUpdate(Connection tx, long id) throws SQLException;

    void updateStatus(Connection tx, long id, ApplicationStatus status, String note) throws SQLException;

    /** Rejects every other pending application for the same pet. */
    int rejectOthers(Connection tx, long petId, long exceptApplicationId, String note) throws SQLException;
}
