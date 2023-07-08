package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserStorageImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(insert.executeAndReturnKey(userToMap(user)).intValue());
        log.info("/POST. User with id = {} created", user.getId());
        return user;
    }

    @Override
    public User delete(User user) {
        String sqlQuery =
                "DELETE " +
                        "FROM USERS " +
                        "WHERE USER_ID=?";
        int result = jdbcTemplate.update(sqlQuery, user.getId());
        if (result == 0) {
            log.error("/DELETE. User with id = {} to be deleted not found", user.getId());
            throw new EntityNotFoundException("User with id = " + user.getId() + " not found");
        }
        log.info("/DELETE. User with id = {} deleted", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery =
                "UPDATE USERS " +
                        "SET LOGIN=?, NAME=?, EMAIL=?, BIRTHDAY=? " +
                        "WHERE USER_ID=?";
        int result = jdbcTemplate.update(sqlQuery,
                user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(),
                user.getId());
        if (result == 0) {
            log.error("/UPDATE. User with id = {} to be updated not found", user.getId());
            throw new EntityNotFoundException("User with id = " + user.getId() + " not found");
        }
        log.info("/UPDATE. User with id = {} updated", user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery =
                "SELECT USER_ID, LOGIN, NAME, EMAIL, BIRTHDAY " +
                        "FROM USERS ";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        log.info("/GET. Users " + (users.isEmpty() ? "is empty" : "found"));
        return users;
    }

    @Override
    public User findUserById(int userId) {
        String sqlQuery =
                "SELECT *" +
                        "FROM USERS " +
                        "WHERE USER_ID=?";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
            log.info("/GET. User with id = {} found", userId);
            return user;
        } catch (DataAccessException e) {
            log.error("User with id = {} not found", userId);
            throw new EntityNotFoundException("User not found");
        }
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("birthday", user.getBirthday());
        return values;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
