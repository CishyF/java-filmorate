package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.ReviewLikeRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class ReviewLikeRepositoryImpl implements ReviewLikeRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveLikes(Review review) {
        final int reviewId = review.getId();
        for (int likedUserId : review.getLikedIds().keySet()) {
            int likeValue = review.getLikedIds().get(likedUserId);
            saveLike(reviewId, likedUserId, likeValue);
        }
    }

    private void saveLike(int filmId, int likedUserId, int likeValue) {
        String sqlQuery = "INSERT INTO review_like (review_id, user_id,like) VALUES (?, ?, ?)";
        jdbcTemplate.update(
                sqlQuery,
                filmId,
                likedUserId,
                likeValue
        );
    }

    @Override
    public void loadLikes(List<Review> reviews) {
        String inSql = String.join(", ", Collections.nCopies(reviews.size(), "?"));
        String sqlQuery = String.format("SELECT * FROM review_like as rl " +
                "LEFT JOIN \"user\" AS u ON fl.user_id = u.id " +
                "WHERE u.login IS NOT NULL AND rl.review_id IN (%s)", inSql);
        Map<Integer, Review> reviewById = reviews.stream().collect(toMap(Review::getId, identity()));
        jdbcTemplate.query(
                sqlQuery,
                (rs) -> {
                    final int filmId = rs.getInt("film_id");
                    final int userId = rs.getInt("user_id");
                    final int like = rs.getInt("like");
                    Review review = reviewById.get(filmId);
                    if (review != null) {
                        if (like > 0)
                            review.addLike(userId);
                        else
                            review.addDislike(userId);
                    }
                },
                reviewById.keySet().toArray()
        );
    }

    @Override
    public List<Integer> findLikesByReviewId(int reviewId) {
        String sqlQuery = "SELECT user_id FROM review_like WHERE review_id = ?";
        List<Integer> likes = jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> rs.getInt("user_id"),
                reviewId
        );
        return likes;
    }

    @Override
    public void deleteLike(Review review, User user) {
        final int reviewId = review.getId();
        final int userId = user.getId();
        String sqlQuery = "DELETE FROM review_like WHERE review_id = ? AND user_id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                reviewId,
                userId
        );
    }

    @Override
    public void deleteLikes(User user) {
        final int userId = user.getId();
        String sqlQuery = "DELETE FROM review_like WHERE user_id = ?;";
        jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    public void deleteLikes(Review review) {
        final int reviewId = review.getId();
        String sqlQuery = "DELETE FROM review_like WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, reviewId);
    }




}
