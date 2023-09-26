package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendRepository {

    void saveFriends(User user);

    void loadFriends(List<User> users);

    List<Integer> findFriendsByUserId(int userId);

    void deleteFriend(User user, User friend);

    void deleteFriends(User user);

    void deleteFriendFromUsers(User friend);
}
