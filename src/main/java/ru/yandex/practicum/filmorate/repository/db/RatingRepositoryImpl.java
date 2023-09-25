package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.RatingRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class RatingRepositoryImpl implements RatingRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Film.RatingMPA> findById(int id) {
        String sqlQuery = "SELECT * FROM rating_mpa WHERE id = ?;";
        Film.RatingMPA rating = jdbcTemplate.queryForObject(
                sqlQuery,
                (rs, rowNum) -> new Film.RatingMPA(
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
    public List<Film.RatingMPA> findAll() {
        String sqlQuery = "SELECT * FROM rating_mpa;";
        List<Film.RatingMPA> ratings = jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> new Film.RatingMPA(
                        rs.getInt("id"), rs.getString("name")
                )
        );
        return ratings;
    }
}
