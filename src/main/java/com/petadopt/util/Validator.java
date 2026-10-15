package com.petadopt.util;

import com.petadopt.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Fluent input validator that collects all errors and throws once. */
public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-() ]{7,20}$");

    private final List<String> errors = new ArrayList<>();

    public Validator required(String value, String label) {
        if (value == null || value.isBlank()) {
            errors.add(label + " is required.");
        }
        return this;
    }

    public Validator maxLength(String value, int max, String label) {
        if (value != null && value.length() > max) {
            errors.add(label + " must be at most " + max + " characters.");
        }
        return this;
    }

    public Validator email(String value) {
        if (value != null && !value.isBlank() && !EMAIL.matcher(value.trim()).matches()) {
            errors.add("Please enter a valid email address.");
        }
        return this;
    }

    /** Phone is optional: validated only when something was entered. */
    public Validator optionalPhone(String value) {
        if (value != null && !value.isBlank() && !PHONE.matcher(value.trim()).matches()) {
            errors.add("Phone number may contain only digits, spaces and + - ( ), 7 to 20 characters.");
        }
        return this;
    }

    public Validator password(String value) {
        if (value == null || value.length() < 8 || !value.matches(".*[A-Za-z].*") || !value.matches(".*[0-9].*")) {
            errors.add("Password must be at least 8 characters and contain letters and digits.");
        }
        return this;
    }

    public Validator check(boolean condition, String message) {
        if (!condition) {
            errors.add(message);
        }
        return this;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public void throwIfInvalid() {
        if (!errors.isEmpty()) {
            throw new ValidationException(new ArrayList<>(errors));
        }
    }

    /** Null-safe trim. */
    public static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
