package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
@Builder
public class Review {
    @JsonProperty("reviewId")
    private int id;
    private String content;
    @JsonProperty("isPositive")
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private int useful;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
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

    public int getUseful() {
        return likedIds.values().stream().reduce(0, Integer::sum);
    }

}
