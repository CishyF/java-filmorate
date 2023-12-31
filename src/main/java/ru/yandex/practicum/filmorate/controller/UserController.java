package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FilmService filmService;

    @GetMapping
    public List<User> getUsers() {
        log.info("Пришел GET-запрос /users");

        List<User> users = userService.findAll();
        log.info("Ответ на GET-запрос /users с телом={}", users);
        return users;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Пришел GET-запрос /users/{id={}}", id);

        User user = userService.findById(id);
        log.info("Ответ на GET-запрос /users/{id={}} с телом={}", id, user);
        return user;
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsOfUser(@PathVariable int id) {
        log.info("Пришел GET-запрос /users/{id={}}/friends", id);

        List<User> friends = userService.getFriendsOfUser(id);
        log.info("Ответ на GET-запрос /users/{id={}}/friends с телом={}", id, friends);
        return friends;
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@PathVariable int id) {
        log.info("Пришел GET-запрос /users/{id={}}/feed", id);

        List<Event> events = userService.getUserFeed(id);
        log.info("Ответ на GET-запрос /users/{id={}}/feed с телом={}", id, events);
        return events;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getFriendsIntersectionOfUsers(@PathVariable int id, @PathVariable int otherId) {
        log.info("Пришел GET-запрос /users/{id={}}/friends/common/{otherId={}}", id, otherId);

        List<User> intersection = userService.getFriendsIntersectionOfUsers(id, otherId);
        log.info(
            "Ответ на GET-запрос /users/{id={}}/friends/common/{otherId={}} с телом={}", id, otherId, intersection
        );
        return intersection;
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendedFilms(@PathVariable("id") int userId) {
        log.info("Пришел GET-запрос /users/{id={}}/recommendations", userId);

        List<Film> recommendedFilms = filmService.getRecommendedFilms(userId);
        log.info("Ответ на GET-запрос /users/{id={}}/recommendations с телом={}", userId, recommendedFilms);
        return recommendedFilms;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Пришел POST-запрос /users с телом={}", user);

        User savedUser = userService.create(user);
        log.info("Пользователь user={} успешно создан", savedUser);
        return savedUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Пришел PUT-запрос /users с телом={}", user);

        User updatedUser = userService.update(user);
        log.info("Пользователь user={} успешно обновлен", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriendToUser(@PathVariable int id, @PathVariable int friendId) {
        log.info("Пришел PUT-запрос /users/{id={}}/friends/{friendId={}}", id, friendId);

        User user = userService.addFriendToUser(id, friendId);
        log.info("Пользователи userId={} и friendId={} успешно добавлены в друзья, тело={}", id, friendId, user);
        return user;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriendOfUser(@PathVariable int id, @PathVariable int friendId) {
        log.info("Пришел DELETE-запрос /users/{id={}}/friends/{friendId={}}", id, friendId);

        userService.removeFriendOfUser(id, friendId);
        log.info("Пользователи userId={} и friendId={} успешно удалены из друзей", id, friendId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable int userId) {
        log.info("Пришел DELETE-запрос /users/{}", userId);

        userService.deleteUserById(userId);
        log.info("Пользователь userId={} успешно удален", userId);
    }

}
