package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.RatingDoesNotExistException;
import ru.yandex.practicum.filmorate.util.validation.AfterBirthdayOfMovie;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {

    private int id;

    private Set<Genre> genres;

    @NotNull
    private RatingMPA mpa;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Length(max = 200, message = "Описание фильма не должно быть более 200 символов")
    private String description;

    @AfterBirthdayOfMovie(message = "Дата релиза не соответствует действительности")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private final Set<Integer> likedIds = new HashSet<>();

    public void addLike(User user) {
        final int userId = user.getId();
        likedIds.add(userId);
    }

    public void removeLike(User user) {
        final int userId = user.getId();
        likedIds.remove(userId);
    }

    public int getAmountOfLikes() {
        return likedIds.size();
    }

    @Data
    public static class Genre {

        private int id;
        @EqualsAndHashCode.Exclude
        private String name;

        public Genre(int id, String name) {
            setId(id);
            this.name = name;
        }

        public void setId(int id) {
            if (id < 1 || id > 6) {
                throw new GenreDoesNotExistException("Получен некорректный id жанра");
            }
            this.id = id;
        }
    }

    @Data
    public static class RatingMPA {

        private int id;
        @EqualsAndHashCode.Exclude
        private String name;

        public RatingMPA(int id, String name) {
            setId(id);
            this.name = name;
        }

        public void setId(int id) {
            if (id < 1 || id > 5) {
                throw new RatingDoesNotExistException("Получен некорректный id рейтинга");
            }
            this.id = id;
        }
    }
}
