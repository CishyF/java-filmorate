package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);

    Optional<Review> findById(int id);

    List<Review> findReviewsByFilmId(int filmId, int count);

    List<Review> findAll();

    void delete(Review review);
}
