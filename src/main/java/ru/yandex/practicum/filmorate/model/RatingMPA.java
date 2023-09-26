package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.exception.RatingDoesNotExistException;

@Data
public class RatingMPA {

    private int id;
    @EqualsAndHashCode.Exclude
    private String name;

    public RatingMPA(int id, String name) {
        setId(id);
        this.name = name;
    }

    public void setId(int id) {
        if (id < 1 || id > 5) {
            throw new RatingDoesNotExistException("Получен некорректный id рейтинга");
        }
        this.id = id;
    }
}
