package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ReviewSaveException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review save(Review review) {
        if (findById(review.getId()).isPresent())
            return update(review);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withSchemaName("public")
                .withTableName("review")
                .usingColumns("content", "is_positive", "user_id", "film_id")
                .usingGeneratedKeyColumns("review_id");
        insert.compile();

        int id = (int) insert.executeAndReturnKey(
                Map.of(
                        "content", review.getContent(),
                        "is_positive", review.getIsPositive(),
                        "user_id", review.getUserId(),
                        "film_id", review.getFilmId())
        );
        review.setId(id);
        Review savedReview = findById(id)
                .orElseThrow(() -> new ReviewSaveException("Произошла ошибка при сохранении обзора"));
        return savedReview;
    }

    @Override
    public Optional<Review> findById(int id) {
        String sqlQuery = "SELECT * FROM review WHERE review_id = ?";
        ReviewMapper mapper = new ReviewMapper();
        Review review;
        try {
            review = jdbcTemplate.queryForObject(sqlQuery, mapper, id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
        if (review == null) {
            return Optional.empty();
        }
        return Optional.of(review);
    }

    @Override
    public List<Review> findAll() {
        String sqlQuery = "SELECT * FROM review";
        ReviewMapper mapper = new ReviewMapper();
        return jdbcTemplate.query(sqlQuery, mapper);
    }

    @Override
    public List<Review> findReviewsByFilmId(int filmId, int count) {
        String sqlQuery = "SELECT * FROM review WHERE film_id = ? LIMIT ?";
        ReviewMapper mapper = new ReviewMapper();
        return jdbcTemplate.query(
                sqlQuery,
                mapper,
                filmId,
                count
        );
    }

    @Override
    public void delete(Review review) {
        final int reviewId = review.getId();
        String sqlQuery = "DELETE FROM review WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    private Review update(Review review) {
        final int reviewId = review.getId();
        String sqlQuery = "UPDATE review SET content = ?,is_positive = ?  WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                reviewId);
        return findById(reviewId)
                .orElseThrow(() -> new ReviewSaveException("Произошла ошибка при обновлении отзыва"));
    }


    private static class ReviewMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Review.builder()
                    .id(rs.getInt("review_id"))
                    .content(rs.getString("content"))
                    .isPositive(rs.getBoolean("is_positive"))
                    .userId(rs.getInt("user_id"))
                    .filmId(rs.getInt("film_id"))
                    .useful(rs.getInt("useful"))
                    .build();
        }
    }

}
