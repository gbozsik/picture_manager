package hu.ponte.hr.exception;

import lombok.Getter;

@Getter
public class ClientException extends Exception {
    private final String message;

    public ClientException(String message) {
        this.message = message;
    }
}
