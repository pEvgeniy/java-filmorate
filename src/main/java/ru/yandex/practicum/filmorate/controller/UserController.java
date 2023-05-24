package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Getter
    protected final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @PostMapping
    public User create(@RequestBody User user) {
        checkUserValidity(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("User {} added", user.getName());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        checkUserValidity(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Updated user {}", user.getName());
            return user;
        }
        log.warn("User {} to update not found!", user.getName());
        throw new NoSuchElementException("User to be updated not found!");
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Total users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    private void checkUserValidity(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("User email is empty. {}", user);
            throw new InvalidUserException("User email must not be empty!");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("User email should contain @. {}", user);
            throw new InvalidUserException("User email must contain @!");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("User login is empty. {}", user);
            throw new InvalidUserException("User login must not be empty!");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("User login contains whitespaces. {}", user);
            throw new InvalidUserException("User login must not contain whitespaces!");
        }
        if (user.getBirthday() == null) {
            log.warn("User birthday date is null. {}", user);
            throw new InvalidUserException("User birthday must be not null!");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("User birthday date in the future. {}", user);
            throw new InvalidUserException("User birthday must be not in the future!");
        }
    }
}
