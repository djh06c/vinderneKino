package dk.ek.kinoxp.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
