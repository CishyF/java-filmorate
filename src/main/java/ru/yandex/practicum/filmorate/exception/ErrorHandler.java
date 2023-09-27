package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({
        FilmDoesNotExistException.class, UserDoesNotExistException.class, DataAccessException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDoesNotExistExceptions(RuntimeException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), e.getClass());
        log.warn("Обработка исключения с кодом 404 и телом={}", response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), e.getClass());
        log.warn("Обработка исключения с кодом 400 и телом={}", response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Throwable t) {
        ErrorResponse response = new ErrorResponse(t.getMessage(), t.getClass());
        log.warn("Обработка исключения с кодом 500 и телом={}", response);
        return response;
    }
}
