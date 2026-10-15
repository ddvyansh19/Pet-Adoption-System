package com.petadopt.service;

import com.petadopt.dao.PetDAO;
import com.petadopt.dao.PetDAOImpl;
import com.petadopt.exception.AccessDeniedException;
import com.petadopt.exception.ResourceNotFoundException;
import com.petadopt.exception.ValidationException;
import com.petadopt.model.AdoptionStatus;
import com.petadopt.model.ApprovalStatus;
import com.petadopt.model.Pet;
import com.petadopt.util.PhotoStorage;
import com.petadopt.util.Validator;
import java.util.List;
import javax.servlet.http.Part;

public class PetService {
    public static final List<String> TYPES = List.of("Dog", "Cat", "Rabbit", "Bird", "Other");

    private final PetDAO petDAO = new PetDAOImpl();

    // ------------------------------------------------------------ shelter operations
    public long create(long shelterId, String name, String type, String breed, String age, String location,
            String description, Part photo) {
        Pet pet = new Pet();
        pet.setShelterId(shelterId);
        fill(pet, name, type, breed, age, location, description);
        pet.setAdoptionStatus(AdoptionStatus.AVAILABLE);
        pet.setApprovalStatus(ApprovalStatus.PENDING); // every new listing waits for an admin
        pet.setPhotoPath(PhotoStorage.store(photo));
        return petDAO.create(pet);
    }

    public void update(long shelterId, long petId, String name, String type, String breed, String age,
            String location, String description, String adoptionStatus, Part photo) {
        Pet pet = ownedPet(shelterId, petId);
        fill(pet, name, type, breed, age, location, description);
        try {
            pet.setAdoptionStatus(AdoptionStatus.valueOf(Validator.clean(adoptionStatus)));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Please choose a valid adoption status.");
        }
        if (pet.isRejected()) {
            pet.setApprovalStatus(ApprovalStatus.PENDING); // editing a rejected listing resubmits it
        }
        String oldPhoto = pet.getPhotoPath();
        String newPhoto = PhotoStorage.store(photo);
        if (newPhoto != null) {
            pet.setPhotoPath(newPhoto);
        }
        petDAO.update(pet);
        if (newPhoto != null) {
            PhotoStorage.delete(oldPhoto);
        }
    }

    public void delete(long shelterId, long petId) {
        Pet pet = ownedPet(shelterId, petId);
        petDAO.delete(petId, shelterId);
        PhotoStorage.delete(pet.getPhotoPath());
    }

    public Pet ownedPet(long shelterId, long petId) {
        Pet pet = petDAO.findById(petId).orElseThrow(() -> new ResourceNotFoundException("Pet #" + petId));
        if (pet.getShelterId() != shelterId) {
            throw new AccessDeniedException();
        }
        return pet;
    }

    public List<Pet> listForShelter(long shelterId) {
        return petDAO.findByShelter(shelterId);
    }

    // ------------------------------------------------------------ admin operations
    public List<Pet> listForAdmin(String statusFilter) {
        if (statusFilter == null || statusFilter.isBlank()) {
            return petDAO.findAll(null);
        }
        try {
            return petDAO.findAll(ApprovalStatus.valueOf(statusFilter.trim()));
        } catch (IllegalArgumentException e) {
            return petDAO.findAll(null);
        }
    }

    public Pet decide(long petId, boolean approve) {
        Pet pet = petDAO.findById(petId).orElseThrow(() -> new ResourceNotFoundException("Pet #" + petId));
        petDAO.setApproval(petId, approve ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        return pet;
    }

    // ------------------------------------------------------------ public browsing
    public List<Pet> search(String type, String breed, String location) {
        return petDAO.search(type, breed, location);
    }

    /** A pet page is public only once an admin approved the listing. */
    public Pet getPublic(long petId) {
        Pet pet = petDAO.findById(petId).orElseThrow(() -> new ResourceNotFoundException("Pet #" + petId));
        if (!pet.isApproved()) {
            throw new ResourceNotFoundException("Pet #" + petId);
        }
        return pet;
    }

    // ------------------------------------------------------------ helpers
    private void fill(Pet pet, String name, String type, String breed, String age, String location,
            String description) {
        double ageYears = -1;
        try {
            ageYears = Double.parseDouble(Validator.clean(age));
        } catch (NumberFormatException ignored) {
            // reported below
        }
        new Validator()
                .required(name, "Pet name").maxLength(name, 100, "Pet name")
                .check(TYPES.contains(Validator.clean(type)), "Please choose a pet type.")
                .required(breed, "Breed").maxLength(breed, 100, "Breed")
                .check(ageYears >= 0 && ageYears <= 40, "Age must be a number between 0 and 40 (years).")
                .required(location, "Location").maxLength(location, 120, "Location")
                .maxLength(description, 2000, "Description")
                .throwIfInvalid();
        pet.setName(name.trim());
        pet.setType(type.trim());
        pet.setBreed(breed.trim());
        pet.setAgeYears(ageYears);
        pet.setLocation(location.trim());
        pet.setDescription(Validator.clean(description));
    }
}
