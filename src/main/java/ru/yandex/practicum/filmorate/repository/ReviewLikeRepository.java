package ru.yandex.practicum.filmorate.repository;


import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface ReviewLikeRepository {
    void saveLikes(Review review);

    void loadLikes(List<Review> reviews);

    List<Integer> findLikesByReviewId(int reviewId);

    void deleteLike(Review review, User user);

    void deleteLikes(User user);

    void deleteLikes(Review review);
}
