package com.petadopt.dao;

import com.petadopt.model.Role;
import com.petadopt.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    long create(User user);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    /** @param role optional filter; null returns everyone */
    List<User> findAll(Role role);

    /** Updates name, email, role, phone and address. */
    void update(User user);

    void updateProfile(long id, String name, String phone, String address);

    void updatePassword(long id, String passwordHash);

    boolean delete(long id);

    boolean emailExists(String email, long excludeId);

    /** Adopters who have applied for any of this shelter's pets. */
    List<User> findAdoptersForShelter(long shelterId);

    /** Shelters that own pets this adopter has applied for. */
    List<User> findSheltersForAdopter(long adopterId);
}
