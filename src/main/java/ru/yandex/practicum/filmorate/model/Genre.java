package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;

@Data
public class Genre {

    private int id;
    @EqualsAndHashCode.Exclude
    private String name;

    public Genre(int id, String name) {
        setId(id);
        this.name = name;
    }

    public void setId(int id) {
        if (id < 1 || id > 6) {
            throw new GenreDoesNotExistException("Получен некорректный id жанра");
        }
        this.id = id;
    }
}
