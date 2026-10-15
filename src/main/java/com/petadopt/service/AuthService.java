package com.petadopt.service;

import com.petadopt.dao.UserDAO;
import com.petadopt.dao.UserDAOImpl;
import com.petadopt.exception.AppException;
import com.petadopt.exception.DuplicateEmailException;
import com.petadopt.exception.ValidationException;
import com.petadopt.model.Role;
import com.petadopt.model.User;
import com.petadopt.util.PasswordUtil;
import com.petadopt.util.Validator;

public class AuthService {
    private final UserDAO userDAO = new UserDAOImpl();
    private final SettingService settings = SettingService.getInstance();

    public User login(String email, String password) {
        new Validator().required(email, "Email").required(password, "Password").throwIfInvalid();
        User user = userDAO.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ValidationException("Invalid email or password."));
        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new ValidationException("Invalid email or password.");
        }
        return user;
    }

    /** Public self-registration: only ADOPTER and SHELTER accounts can be created here. */
    public User register(String name, String email, String password, String confirm, String phone, String address,
            String roleParam) {
        if (!settings.getBoolean(SettingService.ALLOW_REGISTRATION)) {
            throw new AppException("New registrations are currently closed.");
        }
        Role role;
        try {
            role = Role.valueOf(Validator.clean(roleParam));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Please choose whether you are an adopter or a shelter.");
        }
        if (role == Role.ADMIN) {
            throw new ValidationException("Please choose whether you are an adopter or a shelter.");
        }
        new Validator()
                .required(name, "Name").maxLength(name, 100, "Name")
                .required(email, "Email").email(email).maxLength(email, 150, "Email")
                .password(password)
                .check(password != null && password.equals(confirm), "Passwords do not match.")
                .optionalPhone(phone).maxLength(address, 255, "Address")
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
        user.setId(userDAO.create(user));
        return user;
    }
}
