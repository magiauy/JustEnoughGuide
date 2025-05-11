package com.balugaq.jeg.api.objects.exceptions;

public class ArgumentMissingException extends RuntimeException {
    public ArgumentMissingException() {
        super();
    }

    public ArgumentMissingException(String message) {
        super(message);
    }

    public ArgumentMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
