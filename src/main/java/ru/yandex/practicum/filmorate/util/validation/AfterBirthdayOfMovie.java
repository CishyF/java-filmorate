package ru.yandex.practicum.filmorate.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterBirthdayOfMovieValidator.class)
@Documented
public @interface AfterBirthdayOfMovie {
    String message() default "{ru.yandex.practicum.filmorate.util.validation.AfterBirthdayOfMovie.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
