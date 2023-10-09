package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmDirectorRepository;

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
public class FilmDirectorRepositoryImpl implements FilmDirectorRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveDirectors(Film film) {
        final int filmId = film.getId();
        List<Integer> directorIds = getDirectorIds(film.getDirectors());
        for (int directorId : directorIds) {
            saveDirector(filmId, directorId);
        }
    }

    private void saveDirector(int filmId, int directorId) {
        String sqlQuery = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?);";
        jdbcTemplate.update(
                sqlQuery,
                filmId,
                directorId
        );
    }

    private List<Integer> getDirectorIds(Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return Collections.emptyList();
        }
        return directors.stream()
                .map(Director::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void loadDirectors(List<Film> films) {
        String inSql = String.join(", ", Collections.nCopies(films.size(), "?"));
        String sqlQuery = String.format("SELECT * FROM film_director AS fd " +
                "JOIN director AS d ON fd.director_id = d.id WHERE fd.film_id IN (%s)", inSql);
        Map<Integer, Film> filmById = films.stream().collect(toMap(Film::getId, identity()));
        jdbcTemplate.query(
                sqlQuery,
                (rs) -> {
                    final int filmId = rs.getInt("film_id");
                    Film film = filmById.get(filmId);
                    if (film != null) {
                        film.addDirector(makeDirector(rs, 0));
                    }
                },
                filmById.keySet().toArray()
        );
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public void deleteDirectors(Film film) {
        final int filmId = film.getId();
        String sqlQuery = "DELETE FROM film_director WHERE film_id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                filmId
        );
    }

    @Override
    public void deleteFilmsOfDirectorById(int id) {
        String sqlQuery = "DELETE FROM film_director WHERE director_id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                id
        );
    }
}
