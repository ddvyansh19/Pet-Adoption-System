package com.petadopt.model;

import java.sql.Timestamp;

/** An adopter's request to adopt a pet. Joined fields (pet/shelter/adopter names) are read-only helpers. */
public class Application {
    private long id;
    private long petId;
    private String petName;
    private String petPhoto;
    private long shelterId;
    private String shelterName;
    private long adopterId;
    private String adopterName;
    private String adopterEmail;
    private String reason;
    private String housingType;
    private boolean hasOtherPets;
    private String experience;
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private String shelterNote;
    private Timestamp appliedAt;
    private Timestamp updatedAt;

    public boolean isPending() {
        return status == ApplicationStatus.PENDING;
    }

    public boolean isApproved() {
        return status == ApplicationStatus.APPROVED;
    }

    public boolean isRejected() {
        return status == ApplicationStatus.REJECTED;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPetId() {
        return petId;
    }

    public void setPetId(long petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetPhoto() {
        return petPhoto;
    }

    public void setPetPhoto(String petPhoto) {
        this.petPhoto = petPhoto;
    }

    public long getShelterId() {
        return shelterId;
    }

    public void setShelterId(long shelterId) {
        this.shelterId = shelterId;
    }

    public String getShelterName() {
        return shelterName;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }

    public long getAdopterId() {
        return adopterId;
    }

    public void setAdopterId(long adopterId) {
        this.adopterId = adopterId;
    }

    public String getAdopterName() {
        return adopterName;
    }

    public void setAdopterName(String adopterName) {
        this.adopterName = adopterName;
    }

    public String getAdopterEmail() {
        return adopterEmail;
    }

    public void setAdopterEmail(String adopterEmail) {
        this.adopterEmail = adopterEmail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getHousingType() {
        return housingType;
    }

    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }

    public boolean isHasOtherPets() {
        return hasOtherPets;
    }

    public void setHasOtherPets(boolean hasOtherPets) {
        this.hasOtherPets = hasOtherPets;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getShelterNote() {
        return shelterNote;
    }

    public void setShelterNote(String shelterNote) {
        this.shelterNote = shelterNote;
    }

    public Timestamp getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Timestamp appliedAt) {
        this.appliedAt = appliedAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
