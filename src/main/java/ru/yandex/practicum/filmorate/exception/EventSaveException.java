package ru.yandex.practicum.filmorate.exception;

public class EventSaveException extends RuntimeException {
    public EventSaveException(String message) {
        super(message);
    }
}
