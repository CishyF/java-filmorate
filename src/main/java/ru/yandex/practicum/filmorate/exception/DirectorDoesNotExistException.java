package ru.yandex.practicum.filmorate.exception;

public class DirectorDoesNotExistException extends RuntimeException {

    public DirectorDoesNotExistException(String message) {
        super(message);
    }
}
