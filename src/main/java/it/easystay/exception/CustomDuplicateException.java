package it.easystay.exception;

public class CustomDuplicateException extends RuntimeException {
    private final String field;

    public CustomDuplicateException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
