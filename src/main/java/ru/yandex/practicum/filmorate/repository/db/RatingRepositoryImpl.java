package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.repository.RatingRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class RatingRepositoryImpl implements RatingRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<RatingMPA> findById(int id) {
        String sqlQuery = "SELECT * FROM rating_mpa WHERE id = ?;";
        RatingMPA rating = jdbcTemplate.queryForObject(
                sqlQuery,
                (rs, rowNum) -> new RatingMPA(
                        rs.getInt("id"), rs.getString("name")
                ),
                id
        );
        if (rating == null) {
            return Optional.empty();
        }
        return Optional.of(rating);
    }

    @Override
    public List<RatingMPA> findAll() {
        String sqlQuery = "SELECT * FROM rating_mpa;";
        List<RatingMPA> ratings = jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> new RatingMPA(
                        rs.getInt("id"), rs.getString("name")
                )
        );
        return ratings;
    }
}
