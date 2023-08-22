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
        log.info("Пришел GET-запрос /users без тела");

        if (users.isEmpty()) {
            log.info("Ответ на GET-запрос /users с пустым списком пользователей");
            return Collections.emptyList();
        }

        log.info("Ответ на GET-запрос /users с телом={}", users.values());
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        log.info("Пришел POST-запрос /users с телом={}", user);

        if (user.getName() == null) {
            log.info("Пользователю user={} присвоено имя, соответствующее логину", user);
            user.setName(user.getLogin());
        }

        final int id = ++idCounter;
        user.setId(id);

        users.put(id, user);
        log.info("Пользователь user={} успешно создан", user);
        return user;
    }

    @PutMapping ("/users")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Пришел PUT-запрос /users с телом={}", user);

        final int id = user.getId();

        if (!users.containsKey(id)) {
            log.warn("Попытка обновить несуществующего пользователя user={}", user);
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }

        users.put(id, user);
        log.info("Пользователь user={} успешно обновлен", user);
        return ResponseEntity.ok(user);
    }
}
