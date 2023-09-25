package ru.yandex.practicum.filmorate.repository.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmSaveException;
import ru.yandex.practicum.filmorate.exception.RatingDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmRepositoryImpl implements FilmRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final RatingRepository ratingRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public FilmRepositoryImpl(
            JdbcTemplate jdbcTemplate,
            @Qualifier("userRepositoryImpl") UserRepository userRepository,
            GenreRepository genreRepository,
            RatingRepository ratingRepository,
            LikeRepository likeRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.ratingRepository = ratingRepository;
        this.likeRepository = likeRepository;
    }

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
        genreRepository.saveGenres(film);

        Film savedFilm = findById(id)
                .orElseThrow(() -> new FilmSaveException("Произошла ошибка при сохранении фильма"));
        return savedFilm;
    }

    private Film update(Film film) {
        final int filmId = film.getId();
        String sqlQuery = "UPDATE film SET rating_mpa_id = ?, name = ?, description = ?, " +
                "release_date = ?, duration = ? WHERE id = ?;";
        if (!genreRepository.findGenresByFilmId(filmId).equals(film.getGenres())) {
            genreRepository.deleteGenres(film);
            genreRepository.saveGenres(film);
        }
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
        String sqlQuery = "SELECT * FROM film WHERE id = ?;";

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
        String sqlQuery = "SELECT * FROM film;";

        FilmMapper mapper = new FilmMapper();
        List<Film> films = jdbcTemplate.query(
                sqlQuery,
                mapper
        );
        return films;
    }

    @Override
    public void delete(Film film) {
        final int filmId = film.getId();
        String sqlQuery = "DELETE FROM film WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
        genreRepository.deleteGenres(film);
        likeRepository.deleteLikes(film);
    }

    private class FilmMapper implements RowMapper<Film> {

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            int ratingId = rs.getInt("rating_mpa_id");
            Film.RatingMPA rating = ratingRepository.findById(ratingId)
                    .orElseThrow(() -> new RatingDoesNotExistException("Ошибка при получении рейтинга фильма"));

            int filmId = rs.getInt("id");
            Set<Film.Genre> genres = new HashSet<>(genreRepository.findGenresByFilmId(filmId));
            List<Integer> likes = likeRepository.findLikesByFilmId(filmId);

            Film film = Film.builder()
                    .mpa(rating)
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .duration(rs.getInt("duration"))
                    .id(filmId)
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .genres(genres)
                    .build();
            likes.stream().map(userRepository::findById)
                .map(opt -> opt.orElseThrow(
                        () -> new UserDoesNotExistException("Ошибка при получении пользователя, поставившего лайк")
                )).forEach(film::addLike);

            return film;
        }
    }
}
