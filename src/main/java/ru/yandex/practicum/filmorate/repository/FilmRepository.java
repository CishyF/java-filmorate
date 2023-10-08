package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    Film save(Film film);

    Optional<Film> findById(int id);

    List<Film> findAll();

    default List<Film> foundCommonFilms(int userId, int friendId) {
        return null;
    }

    void delete(Film film);
}
