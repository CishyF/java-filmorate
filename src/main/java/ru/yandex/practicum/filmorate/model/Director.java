package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
public class Director {

    private int id;
    @EqualsAndHashCode.Exclude
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
}
