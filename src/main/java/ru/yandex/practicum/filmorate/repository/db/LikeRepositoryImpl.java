package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.LikeRepository;

import java.util.List;

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
