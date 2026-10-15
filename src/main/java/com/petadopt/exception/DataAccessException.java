package com.petadopt.exception;

import java.sql.SQLException;

/** Wraps a checked {@link SQLException} so DAO callers do not need to declare it. */
public class DataAccessException extends AppException {
    private static final long serialVersionUID = 1L;
    private final boolean duplicateKey;

    public DataAccessException(SQLException cause) {
        super("A database error occurred. Please try again.", cause);
        // MySQL error 1062 = duplicate entry for a unique key
        this.duplicateKey = cause.getErrorCode() == 1062;
    }

    public boolean isDuplicateKey() {
        return duplicateKey;
    }
}
