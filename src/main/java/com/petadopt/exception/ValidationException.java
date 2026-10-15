package com.petadopt.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Thrown when user input fails validation. Carries every problem found, not just the first. */
public class ValidationException extends AppException {
    private static final long serialVersionUID = 1L;
    private final ArrayList<String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>(Collections.singletonList(message));
    }

    public ValidationException(List<String> errors) {
        super(String.join(" ", errors));
        this.errors = new ArrayList<>(errors);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
