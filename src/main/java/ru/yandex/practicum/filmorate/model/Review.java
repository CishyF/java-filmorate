package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class Review {
    private int id;
    private String content;
    private boolean isPositive;
    private int userId;
    private int filmId;
    private int useful;

    private final Map<Integer, Integer> likedIds = new HashMap<>();


    public void addLike(User user) {
        final int userId = user.getId();
        likedIds.put(userId, 1);
    }

    public void addDislike(User user) {
        final int userId = user.getId();
        likedIds.put(userId, -1);
    }

    public void addLike(int userId) {
        likedIds.put(userId, 1);
    }

    public void addDislike(int userId) {
        likedIds.put(userId, -1);
    }


    public void removeLike(User user) {
        final int userId = user.getId();
        likedIds.remove(userId);
    }

    public int getUsefulAmount() {
        return likedIds.values().stream().reduce(0, Integer::sum);
    }

}
