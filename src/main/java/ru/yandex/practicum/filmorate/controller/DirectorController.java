package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Пришел GET-запрос /directors");

        List<Director> directors = directorService.findAll();
        log.info("Ответ на GET-запрос /directors с телом={}", directors);
        return directors;
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable int id) {
        log.info("Пришел GET-запрос /directors/{id={}}", id);

        Director director = directorService.findById(id);
        log.info("Ответ на GET-запрос /directors/{id={}} с телом={}", id, director);
        return director;
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Пришел POST-запрос /directors с телом={}", director);

        Director savedDirector = directorService.create(director);
        log.info("Ответ на POST-запрос /directors с телом={}", savedDirector);
        return savedDirector;
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Пришел PUT-запрос /directors с телом={}", director);

        Director updatedDirector = directorService.update(director);
        log.info("Ответ на PUT-запрос /directors с телом={}", updatedDirector);
        return updatedDirector;
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable int id) {
        log.info("Пришел DELETE-запрос /directors/{id={}}", id);

        directorService.removeDirectorById(id);
        log.info("Режиссер с id={} успешно удален", id);
    }
}
