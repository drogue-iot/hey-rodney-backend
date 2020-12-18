package io.drogue.iot.rodney.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Canonical error information.
 */
@RegisterForReflection
public class ErrorInformation {

    private final String error;
    private final String message;

    public ErrorInformation(final String error, final String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return this.error;
    }

    public String getMessage() {
        return this.message;
    }
}
