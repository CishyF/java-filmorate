package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.EventRepository;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final LikeRepository likeRepository;
    private final EventRepository eventRepository;

    @Autowired
    public UserService(
            @Qualifier("userRepositoryImpl") UserRepository userRepository,
            FriendRepository friendRepository,
            LikeRepository likeRepository,
            EventRepository eventRepository
    ) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.likeRepository = likeRepository;
        this.eventRepository = eventRepository;
    }

    public User create(User user) {
        final String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            log.info("Пользователю user={} присвоено имя, соответствующее логину", user);
            user.setName(user.getLogin());
        }
        User savedUser = userRepository.save(user);
        friendRepository.loadFriends(Collections.singletonList(savedUser));

        return savedUser;
    }

    public User findById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserDoesNotExistException("Попытка получить несуществующего пользователя"));
        friendRepository.loadFriends(Collections.singletonList(user));
        return user;
    }

    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        friendRepository.loadFriends(users);
        return users;
    }

    public User update(User user) {
        final int userId = user.getId();
        userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Попытка обновить несуществующего пользователя"));
        return create(user);
    }

    public User addFriendToUser(int userId, int friendId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserDoesNotExistException("Попытка добавить несуществующему пользователю друга"));
        userRepository.findById(friendId)
            .orElseThrow(() -> new UserDoesNotExistException("Попытка добавить несуществующего пользователя в друзья"));

        friendRepository.loadFriends(Collections.singletonList(user));
        user.addFriend(friendId);

        friendRepository.deleteFriends(user);
        friendRepository.saveFriends(user);
        eventRepository.save(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .type(EventType.FRIEND)
                .operation(EventOperation.ADD)
                .entityId(friendId).build());
        return user;
    }

    public void removeFriendOfUser(int userId, int friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Попытка добавить несуществующему пользователю друга"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserDoesNotExistException("Попытка добавить несуществующего пользователя в друзья"));

        user.removeFriend(friend);
        friendRepository.deleteFriend(user, friend);

        eventRepository.save(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .type(EventType.FRIEND)
                .operation(EventOperation.REMOVE)
                .entityId(friendId).build());
    }

    public List<Event> getUserFeed(int userId) {
        findById(userId);
        List<Event> events = eventRepository.findByUserId(userId);
        return events;

    }

    public List<User> getFriendsOfUser(int id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserDoesNotExistException("Попытка получить друзей несуществующего пользователя"));
        friendRepository.loadFriends(Collections.singletonList(user));

        return user.getFriends().stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getFriendsIntersectionOfUsers(int id1, int id2) {
        List<User> friends1 = getFriendsOfUser(id1);
        List<User> friends2 = getFriendsOfUser(id2);

        friends1.removeIf(friend -> !friends2.contains(friend));
        return friends1;
    }

    public Optional<User> getUserWithMaxFilmMatchesCount(List<Film> films, User user) {
        final int userId = user.getId();

        Map<Integer, Integer> countIntersectionsOfLikedFilmsByUserId = new HashMap<>();
        films.stream()
                .filter(film -> film.getLikedIds().contains(userId))
                .flatMap(film -> film.getLikedIds().stream())
                .filter(likedIds -> likedIds != userId)
                .forEach(uId -> countIntersectionsOfLikedFilmsByUserId
                        .compute(uId, (id, count) -> (count == null) ? 0 : count + 1)
                );

        Map.Entry<Integer, Integer> maxLikeIntersectionsCountEntry =
                countIntersectionsOfLikedFilmsByUserId.entrySet()
                        .stream()
                        .max(Comparator.comparingInt(Map.Entry::getValue))
                        .orElse(null);
        if (maxLikeIntersectionsCountEntry == null) {
            return Optional.empty();
        }
        final int userWithMaxFilmMatchesCount = maxLikeIntersectionsCountEntry.getKey();

        return Optional.of(findById(userWithMaxFilmMatchesCount));
    }

    public void delete(User user) {
        userRepository.delete(user);
        likeRepository.deleteLikes(user);
        friendRepository.deleteFriends(user);
        friendRepository.deleteFriendFromUsers(user);
    }
}
