package org.csbf.security.exceptions;

public class InvalidUuidException extends BaseException {
    public InvalidUuidException(String uuidString) {
        super("Invalid UUID string: " + uuidString);
    }

}