package ru.yandex.practicum.filmorate.service.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("inMemoryUserService")
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public InMemoryUserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User addFriend(int userId, int friendId) {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(friendId);
        user.getFriends().add(friendId);
        log.info("Friend {} added to user {}", friendId, user.getName());
        otherUser.getFriends().add(userId);
        log.info("Friend {} added to user {}", userId, otherUser.getName());
        return user;
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(friendId);
        if (user.getFriends().remove(friendId) && otherUser.getFriends().remove(userId)) {
            log.info("Friend {} deleted from user {}", friendId, user.getName());
            log.info("Friend {} deleted from user {}", userId, otherUser.getName());
            return user;
        }
        log.warn("Friend by id={} to be deleted not found", friendId);
        throw new FriendNotFoundException("Friend not found");
    }

    @Override
    public List<User> findFriends(int userId) {
        User user = userStorage.findUserById(userId);
        Set<Integer> friends = new HashSet<>(user.getFriends());
        List<User> friendsList = friends.stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
        log.info("Found friends of user {}", user.getName());
        return friendsList;
    }

    @Override
    public List<User> findCommonFriends(int userId, int otherUserId) {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherUserId);
        Set<Integer> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());
        List<User> users = commonFriends.stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
        log.info("Found common friends");
        return users;
    }
}
