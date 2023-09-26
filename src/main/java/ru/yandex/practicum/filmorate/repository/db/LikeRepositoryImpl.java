package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.LikeRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveLikes(Film film) {
        final int filmId = film.getId();
        for (int likedUserId : film.getLikedIds()) {
            saveLike(filmId, likedUserId);
        }
    }

    private void saveLike(int filmId, int likedUserId) {
        String sqlQuery = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?);";
        jdbcTemplate.update(
                sqlQuery,
                filmId,
                likedUserId
        );
    }

    @Override
    public void loadLikes(List<Film> films) {
        String inSql = String.join(", ", Collections.nCopies(films.size(), "?"));
        String sqlQuery = String.format("SELECT * FROM film_like as fl " +
                "LEFT JOIN \"user\" AS u ON fl.user_id = u.id " +
                "WHERE u.login IS NOT NULL AND fl.film_id IN (%s);", inSql);
        Map<Integer, Film> filmById = films.stream().collect(toMap(Film::getId, identity()));
        jdbcTemplate.query(
                sqlQuery,
                (rs) -> {
                    final int filmId = rs.getInt("film_id");
                    final int userId = rs.getInt("user_id");
                    Film film = filmById.get(filmId);
                    if (film != null) {
                        film.addLike(userId);
                    }
                },
                filmById.keySet().toArray()
        );
    }

    @Override
    public List<Integer> findLikesByFilmId(int filmId) {
        String sqlQuery = "SELECT user_id FROM film_like WHERE film_id = ?;";
        List<Integer> likes = jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> rs.getInt("user_id"),
                filmId
        );
        return likes;
    }

    @Override
    public void deleteLike(Film film, User user) {
        final int filmId = film.getId();
        final int userId = user.getId();
        String sqlQuery = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                filmId,
                userId
        );
    }

    @Override
    public void deleteLikes(User user) {
        final int userId = user.getId();
        String sqlQuery = "DELETE FROM film_like WHERE user_id = ?;";
        jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    public void deleteLikes(Film film) {
        final int filmId = film.getId();
        String sqlQuery = "DELETE FROM film_like WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
