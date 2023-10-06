package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmSaveException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.repository.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmRepositoryImpl implements FilmRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {
        if (findById(film.getId()).isPresent()) {
            return update(film);
        }
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withSchemaName("public")
                .withTableName("film")
                .usingColumns("rating_mpa_id", "name", "description", "release_date", "duration")
                .usingGeneratedKeyColumns("id");
        insert.compile();

        int id = (int) insert.executeAndReturnKey(Map.of(
                "rating_mpa_id", film.getMpa().getId(),
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration()
        ));
        film.setId(id);

        Film savedFilm = findById(id)
                .orElseThrow(() -> new FilmSaveException("Произошла ошибка при сохранении фильма"));
        return savedFilm;
    }

    private Film update(Film film) {
        final int filmId = film.getId();
        String sqlQuery = "UPDATE film SET rating_mpa_id = ?, name = ?, description = ?, " +
                "release_date = ?, duration = ? WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                film.getMpa().getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                filmId
        );
        Film updatedFilm = findById(filmId)
                .orElseThrow(() -> new FilmSaveException("Произошла ошибка при обновлении фильма"));
        return updatedFilm;
    }

    @Override
    public Optional<Film> findById(int id) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.rating_mpa_id, " +
                "f.duration, f.release_date, r.name AS rating_name FROM film AS f " +
                "JOIN rating_mpa AS r ON f.rating_mpa_id = r.id WHERE f.id = ?;";

        FilmMapper mapper = new FilmMapper();
        Film film;
        try {
            film = jdbcTemplate.queryForObject(
                    sqlQuery,
                    mapper,
                    id
            );
        } catch (DataAccessException e) {
           return Optional.empty();
        }
        if (film == null) {
            return Optional.empty();
        }

        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.rating_mpa_id, " +
                "f.duration, f.release_date, r.name AS rating_name FROM film AS f " +
                "JOIN rating_mpa AS r ON f.rating_mpa_id = r.id;";

        FilmMapper mapper = new FilmMapper();
        List<Film> films = jdbcTemplate.query(
                sqlQuery,
                mapper
        );
        return films;
    }

    @Override
    public List<Film> findTopFilmsByLikes(int count) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.rating_mpa_id, f.duration, f.release_date, " +
                "r.name AS rating_name, COUNT(fl.user_id) " +
                "FROM film AS f " +
                "JOIN rating_mpa AS r ON f.rating_mpa_id = r.id " +
                "LEFT JOIN film_like AS fl ON f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?;";

        FilmRepositoryImpl.FilmMapper mapper = new FilmRepositoryImpl.FilmMapper();
        List<Film> films = jdbcTemplate.query(
                sqlQuery,
                mapper,
                count
        );

        return films;
    }

    @Override
    public List<Film> findTopFilmsByLikesAndGenre(int genreId, int count) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.rating_mpa_id, f.duration, f.release_date, " +
                "r.name AS rating_name, fg.genre_id, COUNT(fl.user_id) " +
                "FROM film AS f " +
                "JOIN rating_mpa AS r ON f.rating_mpa_id = r.id " +
                "JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN film_like AS fl ON f.id = fl.film_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?;";

        FilmRepositoryImpl.FilmMapper mapper = new FilmRepositoryImpl.FilmMapper();
        List<Film> films = jdbcTemplate.query(
                sqlQuery,
                mapper,
                genreId, count
        );

        return films;
    }

    @Override
    public List<Film> findTopFilmsByLikesAndYear(int year, int count) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.rating_mpa_id, f.duration, f.release_date, " +
                "r.name AS rating_name, COUNT(fl.user_id) " +
                "FROM film AS f " +
                "JOIN rating_mpa AS r ON f.rating_mpa_id = r.id " +
                "LEFT JOIN film_like AS fl ON f.id = fl.film_id " +
                "WHERE EXTRACT(YEAR FROM CAST(f.release_date AS date)) = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?;";

        FilmRepositoryImpl.FilmMapper mapper = new FilmRepositoryImpl.FilmMapper();
        List<Film> films = jdbcTemplate.query(
                sqlQuery,
                mapper,
                year, count
        );

        return films;
    }

    @Override
    public List<Film> findTopFilmsByLikesAndGenreAndYear(int genreId, int year, int count) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.rating_mpa_id, f.duration, f.release_date, " +
                "r.name AS rating_name, fg.genre_id, COUNT(fl.user_id) " +
                "FROM film AS f " +
                "JOIN rating_mpa AS r ON f.rating_mpa_id = r.id " +
                "JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN film_like AS fl ON f.id = fl.film_id " +
                "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM CAST(f.release_date AS date)) = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?;";

        FilmRepositoryImpl.FilmMapper mapper = new FilmRepositoryImpl.FilmMapper();
        List<Film> films = jdbcTemplate.query(
                sqlQuery,
                mapper,
                genreId, year, count
        );

        return films;
    }

    @Override
    public void delete(Film film) {
        final int filmId = film.getId();
        String sqlQuery = "DELETE FROM film WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private static class FilmMapper implements RowMapper<Film> {

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Film.builder()
                    .id(rs.getInt("id"))
                    .mpa(makeRating(rs, 0))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .duration(rs.getInt("duration"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .build();
        }

        private RatingMPA makeRating(ResultSet rs, int rowNum) throws SQLException {
            return new RatingMPA(
                    rs.getInt("rating_mpa_id"),
                    rs.getString("rating_name")
            );
        }
    }
}
