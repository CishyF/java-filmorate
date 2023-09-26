package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {

    Optional<RatingMPA> findById(int id);

    List<RatingMPA> findAll();
}
