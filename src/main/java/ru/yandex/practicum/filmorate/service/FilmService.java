package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.*;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final FilmDirectorRepository filmDirectorRepository;
    private final DirectorRepository directorRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Autowired
    public FilmService(
            @Qualifier("filmRepositoryImpl") FilmRepository filmRepository,
            FilmGenreRepository filmGenreRepository,
            FilmDirectorRepository filmDirectorRepository,
            DirectorRepository directorRepository,
            EventRepository eventRepository,
            LikeRepository likeRepository,
            UserService userService
    ) {
        this.filmRepository = filmRepository;
        this.filmGenreRepository = filmGenreRepository;
        this.filmDirectorRepository = filmDirectorRepository;
        this.directorRepository = directorRepository;
        this.eventRepository = eventRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    public Film create(Film film) {
        Film savedFilm = filmRepository.save(film);
        filmGenreRepository.saveGenres(film);
        filmDirectorRepository.saveDirectors(film);

        List<Film> singletonListForLoad = Collections.singletonList(savedFilm);
        filmGenreRepository.loadGenres(singletonListForLoad);
        filmDirectorRepository.loadDirectors(singletonListForLoad);
        likeRepository.loadLikes(singletonListForLoad);

        return savedFilm;
    }

    public Film findById(int id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка получить несуществующий фильм"));

        List<Film> singletonListForLoad = Collections.singletonList(film);
        filmGenreRepository.loadGenres(singletonListForLoad);
        filmDirectorRepository.loadDirectors(singletonListForLoad);
        likeRepository.loadLikes(singletonListForLoad);

        return film;
    }

    public List<Film> findAll() {
        List<Film> films = filmRepository.findAll();
        filmGenreRepository.loadGenres(films);
        filmDirectorRepository.loadDirectors(films);
        likeRepository.loadLikes(films);
        return films;
    }

        public List<Film> getCommonFilms(int userId, int friendId) {
            userService.findById(userId);
            userService.findById(friendId);

            List<Film> films = filmRepository.foundCommonFilms(userId, friendId);
            filmGenreRepository.loadGenres(films);
            filmDirectorRepository.loadDirectors(films);
            likeRepository.loadLikes(films);

            return films;
            }

    public List<Film> searchFilms(String query, List<String> by) {
        for (String b : by) {
            if (!b.equals("director") && !b.equals("title")) {
                throw new FilmDoesNotExistException(
                        "Допустимые значения: director, title. Либо оба значения через запятую.");
            }
        }

        List<Film> films;

        if (by.size() == 1) {
            if (by.contains("director")) {
                films = filmRepository.findTopFilmsByDirector(query);
            } else {
                films = filmRepository.findTopFilmsByName(query);
            }
        } else {
            films = List.copyOf(Stream.concat(filmRepository.findTopFilmsByDirector(query).stream(),
                    filmRepository.findTopFilmsByName(query).stream()).collect(Collectors.toSet()));
        }
        filmGenreRepository.loadGenres(films);
        filmDirectorRepository.loadDirectors(films);
        likeRepository.loadLikes(films);

        return films.stream().sorted(Comparator.comparingInt(Film::getAmountOfLikes).reversed())
                .collect(Collectors.toList());
    }

    public Film update(Film film) {
        final int filmId = film.getId();
        filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка обновить несуществующий фильм"));
        filmGenreRepository.deleteGenres(film);
        filmDirectorRepository.deleteDirectors(film);
        return create(film);
    }

    public Film addLikeToFilm(int filmId, int userId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка поставить лайк несуществующему фильму"));

        List<Film> singletonListForLoad = Collections.singletonList(film);
        filmGenreRepository.loadGenres(singletonListForLoad);
        filmDirectorRepository.loadDirectors(singletonListForLoad);
        likeRepository.loadLikes(singletonListForLoad);

        User user = userService.findById(userId);

        film.addLike(user);
        likeRepository.deleteLikes(film);
        likeRepository.saveLikes(film);
        eventRepository.save(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .type(EventType.LIKE)
                .operation(EventOperation.ADD)
                .entityId(filmId).build());
        return film;
    }

    public void removeLikeFromFilm(int filmId, int userId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка убрать лайк у несуществующего фильма"));
        User user = userService.findById(userId);

        likeRepository.deleteLike(film, user);
        eventRepository.save(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .type(EventType.LIKE)
                .operation(EventOperation.REMOVE)
                .entityId(filmId).build());
    }

    public List<Film> findTopFilmsByLikesOrGenreAndYear(int genreId, int year, int count) {
        List<Film> films;

        if (genreId < 0 || genreId > 6) {
            throw new GenreDoesNotExistException("Получен некорректный id жанра");
        }
        if (year < 0 || year > 0 && year < 1895) {
            throw new GenreDoesNotExistException("Дата релиза должна быть не ранее 1895 года");
        }

        if (genreId > 0 && year == 0) {
            films = filmRepository.findTopFilmsByLikesAndGenre(genreId, count);
        } else if (genreId == 0 && year > 0) {
            films = filmRepository.findTopFilmsByLikesAndYear(year, count);
        } else if (genreId > 0) {
            films = filmRepository.findTopFilmsByLikesAndGenreAndYear(genreId, year, count);
        } else {
            films = filmRepository.findTopFilmsByLikes(count);
        }
        filmGenreRepository.loadGenres(films);
        filmDirectorRepository.loadDirectors(films);
        likeRepository.loadLikes(films);

        return films;
    }

     public List<Film> getDirectorFilmsByLikes(int directorId) {
        return getDirectorFilms(directorId).stream()
                .sorted(Comparator.<Film>comparingInt(film -> film.getLikedIds().size()).reversed())
                .collect(Collectors.toList());
     }

     public List<Film> getDirectorFilmsByYear(int directorId) {
        return getDirectorFilms(directorId).stream()
                .sorted(Comparator.comparing(Film::getReleaseDate))
                .collect(Collectors.toList());
     }

     public List<Film> getDirectorFilms(int directorId) {
        directorRepository.findById(directorId)
            .orElseThrow(() -> new DirectorDoesNotExistException("Попытка получить фильмы несуществующего режиссера"));
        return findAll().stream()
                .filter(film -> film.getDirectors()
                                        .stream()
                                        .map(Director::getId)
                                        .anyMatch(id -> id == directorId)
                ).collect(Collectors.toList());
     }

    public List<Film> getRecommendedFilms(int id) {
        List<Film> films = findAll();
        User user = userService.findById(id);

        Optional<User> userWithMaxFilmMatchesCount = userService.getUserWithMaxFilmMatchesCount(films, user);
        if (userWithMaxFilmMatchesCount.isEmpty()) {
            return Collections.emptyList();
        }
        User other = userWithMaxFilmMatchesCount.get();

        List<Film> newRecommendedFilmsForUser = getMismatchedFilmsOfUserWithOther(user, other);
        return newRecommendedFilmsForUser;
    }

    private List<Film> getMismatchedFilmsOfUserWithOther(User user, User other) {
        final int userId = user.getId();
        final int otherId = other.getId();

        List<Film> otherFilms = getFilmsOfUser(otherId);
        List<Film> userFilms = getFilmsOfUser(userId);
        otherFilms.removeIf(userFilms::contains);

        return otherFilms;
    }

    public List<Film> getFilmsOfUser(int userId) {
        return findAll().stream()
                .filter(film -> film.getLikedIds().contains(userId))
                .collect(Collectors.toList());
    }

    public void delete(Film film) {
        filmGenreRepository.deleteGenres(film);
        filmDirectorRepository.deleteDirectors(film);
        likeRepository.deleteLikes(film);
        filmRepository.delete(film);
    }
}
