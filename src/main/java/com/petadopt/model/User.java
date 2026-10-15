package com.petadopt.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Abstract base for every account. Subclasses {@link Admin}, {@link Shelter} and {@link Adopter}
 * supply role-specific behaviour (dashboard URL, label) through polymorphism.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private String email;
    private String passwordHash;
    private Role role;
    private String phone;
    private String address;
    private Timestamp createdAt;

    protected User(Role role) {
        this.role = role;
    }

    /** Factory: builds the right subclass for a role. */
    public static User create(Role role) {
        switch (role) {
            case ADMIN:
                return new Admin();
            case SHELTER:
                return new Shelter();
            default:
                return new Adopter();
        }
    }

    /** Context-relative URL of this user's dashboard. */
    public abstract String getDashboardPath();

    /** One-line description of what this role can do (shown on the dashboard). */
    public abstract String getRoleDescription();

    public String getRoleLabel() {
        return role.getLabel();
    }

    public String getRoleName() {
        return role.name();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", email=" + email + "}";
    }
}
