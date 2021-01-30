package net.nlacombe.commonlib.exception;

public class RuntimeInterruptedException extends RuntimeException {

    public RuntimeInterruptedException() {
    }

    public RuntimeInterruptedException(String message) {
        super(message);
    }

    public RuntimeInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeInterruptedException(Throwable cause) {
        super(cause);
    }
}
