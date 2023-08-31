package ru.yandex.practicum.filmorate.exception;

import lombok.Value;

@Value
public class ErrorResponse {

    String message;
    Class<? extends Throwable> cause;
}
