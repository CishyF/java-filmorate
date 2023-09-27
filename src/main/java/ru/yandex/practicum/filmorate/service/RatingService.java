package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.RatingDoesNotExistException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.repository.RatingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingMPA findById(int id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new RatingDoesNotExistException("Попытка получить несуществующий рейтинг фильма"));
    }

    public List<RatingMPA> findAll() {
        return ratingRepository.findAll();
    }
}
