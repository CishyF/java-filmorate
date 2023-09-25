package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface LikeRepository {

    void saveLikes(Film film);

    List<Integer> findLikesByFilmId(int filmId);

    void deleteLike(Film film, User user);

    void deleteLikes(User user);

    void deleteLikes(Film film);
}
