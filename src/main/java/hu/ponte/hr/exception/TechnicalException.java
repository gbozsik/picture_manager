package hu.ponte.hr.exception;

import lombok.Getter;

public class TechnicalException extends Exception {

    @Getter
    private final String message;

    public TechnicalException(String message) {
        this.message = message;
    }
}
