package com.balugaq.jeg.api.objects.exceptions;

/**
 * @author balugaq
 * @since 1.6
 */
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
