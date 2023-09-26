package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryImpl implements GenreRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveGenres(Film film) {
        final int filmId = film.getId();
        List<Integer> genreIds = getGenreIds(film.getGenres());
        for (int genreId : genreIds) {
            saveGenreOfFilm(filmId, genreId);
        }
    }

    private void saveGenreOfFilm(int filmId, int genreId) {
        String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";
        jdbcTemplate.update(
                sqlQuery,
                filmId,
                genreId
        );
    }

    private List<Integer> getGenreIds(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return Collections.emptyList();
        }
        return genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> findGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT fg.genre_id, g.name FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.id WHERE fg.film_id = ?;";
        List<Genre> genres = jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> new Genre(
                    rs.getInt("genre_id"), rs.getString("name")
                ),
                filmId
        );
        return genres;
    }

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

    @Override
    public void deleteGenres(Film film) {
        final int filmId = film.getId();
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
