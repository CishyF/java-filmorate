package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public Genre findById(int id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new GenreDoesNotExistException("Попытка получить несуществующий жанр фильма"));
    }

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }
}
