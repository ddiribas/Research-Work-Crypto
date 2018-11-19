package ru.ddiribas.Encryption;

public class StopOperationException extends RuntimeException {
    StopOperationException(String message) {
        super(message);
    }
}
