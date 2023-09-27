package ru.yandex.practicum.filmorate.exception;

public class RatingDoesNotExistException extends RuntimeException {

    public RatingDoesNotExistException(String message) {
        super(message);
    }
}
