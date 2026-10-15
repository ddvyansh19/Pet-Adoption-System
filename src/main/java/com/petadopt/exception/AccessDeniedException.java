package com.petadopt.exception;

public class AccessDeniedException extends AppException {
    private static final long serialVersionUID = 1L;

    public AccessDeniedException() {
        super("You are not allowed to perform this action.");
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
