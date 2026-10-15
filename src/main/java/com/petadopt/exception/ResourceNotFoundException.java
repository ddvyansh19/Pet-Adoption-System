package com.petadopt.exception;

public class ResourceNotFoundException extends AppException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String what) {
        super(what + " was not found.");
    }
}
