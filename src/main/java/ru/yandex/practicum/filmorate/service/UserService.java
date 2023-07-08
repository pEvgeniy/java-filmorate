package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User addFriend(int userId, int friendId);

    User deleteFriend(int userId, int friendId);

    List<User> findFriends(int userId);

    List<User> findCommonFriends(int userId, int otherUserId);
}
