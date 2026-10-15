package com.petadopt.service;

import com.petadopt.dao.UserDAO;
import com.petadopt.dao.UserDAOImpl;
import com.petadopt.exception.AppException;
import com.petadopt.exception.DuplicateEmailException;
import com.petadopt.exception.ResourceNotFoundException;
import com.petadopt.exception.ValidationException;
import com.petadopt.model.Role;
import com.petadopt.model.User;
import com.petadopt.util.PasswordUtil;
import com.petadopt.util.Validator;
import java.util.List;

/** Admin user management plus self-service profile operations. */
public class UserService {
    private final UserDAO userDAO = new UserDAOImpl();

    public static Role parseRole(String value) {
        try {
            return Role.valueOf(Validator.clean(value));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Please select a valid role.");
        }
    }

    public List<User> list(String roleFilter) {
        Role role = (roleFilter == null || roleFilter.isBlank()) ? null : parseRole(roleFilter);
        return userDAO.findAll(role);
    }

    public User get(long id) {
        return userDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("User #" + id));
    }

    public void create(String name, String email, String password, String roleParam, String phone, String address) {
        Role role = parseRole(roleParam);
        new Validator()
                .required(name, "Name").maxLength(name, 100, "Name")
                .required(email, "Email").email(email).maxLength(email, 150, "Email")
                .password(password).optionalPhone(phone).maxLength(address, 255, "Address")
                .throwIfInvalid();
        String normalisedEmail = email.trim().toLowerCase();
        if (userDAO.emailExists(normalisedEmail, 0)) {
            throw new DuplicateEmailException(normalisedEmail);
        }
        User user = User.create(role);
        user.setName(name.trim());
        user.setEmail(normalisedEmail);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setPhone(phone);
        user.setAddress(address);
        userDAO.create(user);
    }

    public void update(long id, String name, String email, String roleParam, String phone, String address,
            String newPassword, long actingAdminId) {
        Role role = parseRole(roleParam);
        User existing = get(id);
        new Validator()
                .required(name, "Name").maxLength(name, 100, "Name")
                .required(email, "Email").email(email).maxLength(email, 150, "Email")
                .optionalPhone(phone).maxLength(address, 255, "Address")
                .check(newPassword == null || newPassword.isEmpty() || newPassword.length() >= 8,
                        "A new password must be at least 8 characters.")
                .throwIfInvalid();
        if (id == actingAdminId && role != Role.ADMIN) {
            throw new ValidationException("You cannot remove your own administrator role.");
        }
        String normalisedEmail = email.trim().toLowerCase();
        if (userDAO.emailExists(normalisedEmail, id)) {
            throw new DuplicateEmailException(normalisedEmail);
        }
        // Role is fixed per subclass, so build a fresh instance of the requested role.
        User updated = User.create(role);
        updated.setId(existing.getId());
        updated.setName(name.trim());
        updated.setEmail(normalisedEmail);
        updated.setPhone(phone);
        updated.setAddress(address);
        userDAO.update(updated);
        if (newPassword != null && !newPassword.isEmpty()) {
            userDAO.updatePassword(id, PasswordUtil.hash(newPassword));
        }
    }

    public void delete(long id, long actingAdminId) {
        if (id == actingAdminId) {
            throw new ValidationException("You cannot delete your own account.");
        }
        if (!userDAO.delete(id)) {
            throw new ResourceNotFoundException("User #" + id);
        }
    }

    /** Updates the signed-in user's own profile and returns the refreshed account. */
    public User updateProfile(long userId, String name, String phone, String address) {
        new Validator().required(name, "Name").maxLength(name, 100, "Name").optionalPhone(phone)
                .maxLength(address, 255, "Address").throwIfInvalid();
        userDAO.updateProfile(userId, name.trim(), phone, address);
        return get(userId);
    }

    public void changePassword(long userId, String current, String next, String confirm) {
        User user = get(userId);
        if (!PasswordUtil.verify(current, user.getPasswordHash())) {
            throw new ValidationException("Your current password is incorrect.");
        }
        new Validator().password(next).check(next != null && next.equals(confirm), "New passwords do not match.")
                .throwIfInvalid();
        userDAO.updatePassword(userId, PasswordUtil.hash(next));
    }

    /** Guard used by services that need a user to exist and hold a given role. */
    public User requireRole(long id, Role role) {
        User user = get(id);
        if (user.getRole() != role) {
            throw new AppException("User #" + id + " is not a " + role.getLabel().toLowerCase() + ".");
        }
        return user;
    }
}
