package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public UserService(
            @Qualifier("userRepositoryImpl") UserRepository userRepository,
            FriendRepository friendRepository,
            LikeRepository likeRepository
    ) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.likeRepository = likeRepository;
    }

    public User create(User user) {
        final String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            log.info("Пользователю user={} присвоено имя, соответствующее логину", user);
            user.setName(user.getLogin());
        }

        return userRepository.save(user);
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserDoesNotExistException("Попытка получить несуществующего пользователя"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
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
        User friend = userRepository.findById(friendId)
            .orElseThrow(() -> new UserDoesNotExistException("Попытка добавить несуществующего пользователя в друзья"));

        user.addFriend(friendId);
        friendRepository.deleteFriends(user);
        friendRepository.saveFriends(user);
        return user;
    }

    public void removeFriendOfUser(int userId, int friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Попытка добавить несуществующему пользователю друга"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserDoesNotExistException("Попытка добавить несуществующего пользователя в друзья"));

        user.removeFriend(friend);
        friendRepository.deleteFriend(user, friend);
    }

    public List<User> getFriendsOfUser(int id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserDoesNotExistException("Попытка получить друзей несуществующего пользователя"));

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

    public void delete(User user) {
        userRepository.delete(user);
        likeRepository.deleteLikes(user);
        friendRepository.deleteFriends(user);
        friendRepository.deleteFriendFromUsers(user);
    }
}
