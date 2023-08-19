package ru.yandex.practicum.filmorate.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterBirthdayOfMovieValidator implements ConstraintValidator<AfterBirthdayOfMovie, LocalDate> {

    @Override
    public void initialize(AfterBirthdayOfMovie constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(LocalDate.of(1895, 12, 27));
    }
}
