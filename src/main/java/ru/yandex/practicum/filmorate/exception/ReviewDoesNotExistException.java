package ru.yandex.practicum.filmorate.exception;

public class ReviewDoesNotExistException extends RuntimeException {
    public ReviewDoesNotExistException(String message) {
        super(message);
    }
}
