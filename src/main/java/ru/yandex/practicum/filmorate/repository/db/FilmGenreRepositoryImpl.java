package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.FilmGenreRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class FilmGenreRepositoryImpl implements FilmGenreRepository {

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
    public void loadGenres(List<Film> films) {
        String inSql = String.join(", ", Collections.nCopies(films.size(), "?"));
        String sqlQuery = String.format("SELECT * FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.id WHERE fg.film_id IN (%s);", inSql);
        Map<Integer, Film> filmById = films.stream().collect(toMap(Film::getId, identity()));
        jdbcTemplate.query(
                sqlQuery,
                (rs) -> {
                    final int filmId = rs.getInt("film_id");
                    Film film = filmById.get(filmId);
                    if (film != null) {
                        film.addGenre(makeGenre(rs, 0));
                    }
                },
                filmById.keySet().toArray()
        );
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        );
    }

    @Override
    public List<Genre> findGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT fg.genre_id, g.name FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.id WHERE fg.film_id = ?;";
        List<Genre> genres = jdbcTemplate.query(
                sqlQuery,
                this::makeGenre,
                filmId
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
