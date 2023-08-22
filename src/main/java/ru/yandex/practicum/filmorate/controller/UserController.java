package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
public class UserController {

    private Map<Integer, User> users = new HashMap<>();
    private int idCounter = 0;

    @GetMapping("/users")
    public List<User> getUsers() {
        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        final int id = ++idCounter;
        user.setId(id);

        users.put(id, user);
        log.info("Пользователь с id={} успешно создан", id);
        return user;
    }

    @PutMapping ("/users")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        final int id = user.getId();

        if (!users.containsKey(id)) {
            log.warn("Попытка обновить несуществующего пользователя");
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }

        users.put(id, user);
        log.info("Пользователь с id={} успешно обновлен", id);
        return ResponseEntity.ok(user);
    }
}
