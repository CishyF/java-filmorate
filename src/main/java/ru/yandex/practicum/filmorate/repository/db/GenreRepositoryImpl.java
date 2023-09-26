package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryImpl implements GenreRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> findById(int id) {
        String sqlQuery = "SELECT * FROM genre WHERE id = ?;";
        Genre genre = jdbcTemplate.queryForObject(
                sqlQuery,
                (rs, rowNum) -> new Genre(
                        rs.getInt("id"), rs.getString("name")
                ),
                id
        );
        if (genre == null) {
            return Optional.empty();
        }
        return Optional.of(genre);
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT * FROM genre;";
        List<Genre> genres = jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> new Genre(
                        rs.getInt("id"), rs.getString("name")
                )
        );
        return genres;
    }
}
