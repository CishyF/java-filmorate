package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.FilmDirectorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final FilmDirectorRepository filmDirectorRepository;

    public Director create(Director director) {
        return directorRepository.save(director);
    }

    public Director findById(int id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new DirectorDoesNotExistException("Попытка получить несуществующего режиссера"));
    }

    public List<Director> findAll() {
        return directorRepository.findAll();
    }

    public Director update(Director director) {
        final int directorId = director.getId();
        directorRepository.findById(directorId)
                .orElseThrow(() -> new DirectorDoesNotExistException("Попытка обновить несуществующего режиссера"));
        return create(director);
    }

    public void removeDirectorById(int id) {
        filmDirectorRepository.deleteFilmsOfDirectorById(id);
        directorRepository.deleteDirectorById(id);
    }
}
