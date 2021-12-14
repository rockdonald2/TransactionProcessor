package edu.utils.exception;

public class PropertyProviderException extends RuntimeException {

    public PropertyProviderException() {
        super("Failed to set property in configuration.");
    }

}
