package ru.yandex.practicum.filmorate.exception;

public class EventDoesNotExistException extends RuntimeException {
    public EventDoesNotExistException(String message) {
        super(message);
    }
}
