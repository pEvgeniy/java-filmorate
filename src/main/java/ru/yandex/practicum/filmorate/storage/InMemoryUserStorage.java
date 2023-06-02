package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    @Getter
    protected final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("User {} added", user.getName());
        return user;
    }

    @Override
    public User delete(User user) {
        User foundUser = users.get(user.getId());
        if (foundUser != null) {
            users.remove(user.getId());
            log.info("User {} removed", user.getName());
            return user;
        }
        log.warn("User {} to delete not found.", user.getName());
        throw new UserNotFoundException("User not found.");
    }

    @Override
    public User update(User user) {
        return users.keySet().stream()
                .filter(u -> u.equals(user.getId()))
                .findFirst()
                .map(u -> {
                    users.put(user.getId(), user);
                    log.info("Updated user {}", user.getName());
                    return user;
                })
                .orElseThrow(() -> {
                    log.warn("User {} to update not found.", user.getName());
                    return new UserNotFoundException("User not found.");
                });
    }

    @Override
    public List<User> findAll() {
        log.info("Total users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(int userId) {
        User foundUser = users.get(userId);
        if (foundUser != null) {
            log.info("Found by id user {}", foundUser);
            return foundUser;
        }
        log.warn("User by id={} not found", userId);
        throw new UserNotFoundException("User not found.");
    }
}
