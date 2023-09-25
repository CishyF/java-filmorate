package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {

    Optional<Film.RatingMPA> findById(int id);

    List<Film.RatingMPA> findAll();
}
