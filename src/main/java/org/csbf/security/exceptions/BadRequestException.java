package org.csbf.security.exceptions;

public class BadRequestException extends BaseException{
    public BadRequestException(String message) {
        super(message);
    }

    public static class InvalidAuthenticationRequestException extends BadRequestException {
        public InvalidAuthenticationRequestException(String message) {
            super("Forbidden Request: " + message);
        }
    }
}
