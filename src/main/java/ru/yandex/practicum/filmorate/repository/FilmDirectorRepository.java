package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDirectorRepository {

    void saveDirectors(Film film);

    void loadDirectors(List<Film> films);

    void deleteDirectors(Film film);

    void deleteFilmsOfDirectorById(int id);
}
