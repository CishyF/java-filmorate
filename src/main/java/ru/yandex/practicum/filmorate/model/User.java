package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {

    private int id;

    @Email(message = "Электронная почта указана неверно")
    private String email;

    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелов")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private final Set<Integer> friends = new HashSet<>();

    public void addFriend(User user) {
        final int userId = user.getId();
        if (friends.add(userId)) {
            user.addFriend(this);
        }
    }

    public void addFriend(int userId) {
        friends.add(userId);
    }

    public void removeFriend(User user) {
        final int userId = user.getId();
        if (friends.remove(userId)) {
            user.removeFriend(this);
        }
    }

    public int getAmountOfFriends() {
        return friends.size();
    }
}
