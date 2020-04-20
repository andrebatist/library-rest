package ru.aplaksin.libraryrest.exception;

public class BadDtoException extends Exception{
    private static final long serialVersionUID = 1L;

    public BadDtoException(String message) {
        super(message);
    }
}
