package com.petadopt.dao;

import com.petadopt.model.AdoptionStatus;
import com.petadopt.model.ApprovalStatus;
import com.petadopt.model.Pet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PetDAO {
    long create(Pet pet);

    Optional<Pet> findById(long id);

    /** Updates the shelter-editable fields (not the approval status). */
    void update(Pet pet);

    void setApproval(long petId, ApprovalStatus status);

    boolean delete(long petId, long shelterId);

    List<Pet> findByShelter(long shelterId);

    /** @param approval optional filter; null returns every listing */
    List<Pet> findAll(ApprovalStatus approval);

    /** Public search: only approved and still available pets. Blank criteria are ignored. */
    List<Pet> search(String type, String breed, String location);

    List<String> findDistinctTypes();

    /**
     * Transaction-aware: marks the pet adopted only if it is still available.
     * @return number of rows changed (0 means somebody else already adopted it)
     */
    int markAdopted(Connection tx, long petId) throws SQLException;

    AdoptionStatus statusOf(long petId);
}
