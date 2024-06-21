package ru.pavel.net;

public class InternetAddressException extends RuntimeException {
    public InternetAddressException(String message) {
        super(message);
    }

    public InternetAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
