package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.util.validation.AfterBirthdayOfMovie;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Length(max = 200, message = "Описание фильма не должно быть более 200 символов")
    private String description;

    @AfterBirthdayOfMovie(message = "Дата релиза не соответствует действительности")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    @EqualsAndHashCode.Exclude
    private Set<Integer> likedIds = new HashSet<>();

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
}
