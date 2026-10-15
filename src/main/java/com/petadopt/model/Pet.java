package com.petadopt.model;

import java.sql.Timestamp;

public class Pet {
    private long id;
    private long shelterId;
    private String shelterName;
    private String name;
    private String type;
    private String breed;
    private double ageYears;
    private String location;
    private String description;
    private String photoPath;
    private AdoptionStatus adoptionStatus = AdoptionStatus.AVAILABLE;
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
    private Timestamp createdAt;

    /** Human friendly age, e.g. "8 months" or "2 years". */
    public String getAgeLabel() {
        if (ageYears < 1) {
            long months = Math.max(1, Math.round(ageYears * 12));
            return months + (months == 1 ? " month" : " months");
        }
        String value = (ageYears == Math.floor(ageYears)) ? String.valueOf((long) ageYears) : String.valueOf(ageYears);
        return value + (ageYears == 1 ? " year" : " years");
    }

    public boolean isPending() {
        return approvalStatus == ApprovalStatus.PENDING;
    }

    public boolean isApproved() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }

    public boolean isRejected() {
        return approvalStatus == ApprovalStatus.REJECTED;
    }

    public boolean isAvailable() {
        return adoptionStatus == AdoptionStatus.AVAILABLE;
    }

    public boolean isAdopted() {
        return adoptionStatus == AdoptionStatus.ADOPTED;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public double getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(double ageYears) {
        this.ageYears = ageYears;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public AdoptionStatus getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(AdoptionStatus adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
