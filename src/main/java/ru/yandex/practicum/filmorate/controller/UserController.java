package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Getter
    protected final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("User {} added", user.getName());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return Optional.ofNullable(users.get(user.getId()))
                .map(existingUser -> {
                    users.put(user.getId(), user);
                    log.info("Updated user {}", user.getName());
                    return user;
                })
                .orElseThrow(() -> {
                    log.warn("User {} to update not found!", user.getName());
                    return new NoSuchElementException("User to be updated not found!");
                });
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Total users: {}", users.size());
        return new ArrayList<>(users.values());
    }
}
