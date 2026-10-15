package com.petadopt.exception;

public class DuplicateEmailException extends AppException {
    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String email) {
        super("An account with the email '" + email + "' already exists.");
    }
}
