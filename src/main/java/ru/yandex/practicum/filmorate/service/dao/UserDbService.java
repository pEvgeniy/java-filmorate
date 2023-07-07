package ru.yandex.practicum.filmorate.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Qualifier("userDbService")
public class UserDbService implements UserService {
    UserStorage userStorage;
    JdbcTemplate jdbcTemplate;

    public UserDbService(@Qualifier("userDbStorage") UserStorage userStorage,
                         JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addFriend(int userId, int friendId) {
        String sqlQuery =
                "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
        int result = jdbcTemplate.update(sqlQuery, userId, friendId);
        if (result == 0) {
            log.error("/PUT. Friend(id = {}) or user(id = {}) not found", friendId, userId);
            throw new FriendNotFoundException("Friend not found");
        }
        log.error("/PUT. Friend added");
        setFriendshipStatus(userId, friendId);
        return userStorage.findUserById(userId);
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        String sqlQuery =
                "DELETE " +
                        "FROM FRIENDS " +
                        "WHERE USER_ID=? AND FRIEND_ID=?";
        int result = jdbcTemplate.update(sqlQuery, userId, friendId);
        if (result == 0) {
            log.error("/DELETE. Friend(id = {}) or user(id = {}) to be deleted not found", friendId, userId);
            throw new FriendNotFoundException("Friend not found");
        }
        log.info("/DELETE. Friend deleted");
        setFriendshipStatus(userId, friendId);
        return userStorage.findUserById(userId);
    }

    @Override
    public List<User> findFriends(int userId) {
        String sqlQuery =
                "SELECT U.* " +
                        "FROM USERS U " +
                        "WHERE USER_ID IN (" +
                        "SELECT FRIEND_ID " +
                        "FROM FRIENDS F " +
                        "WHERE F.USER_ID=?)";
        List<User> friends = jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
        log.info("/GET. Friends " + (friends.isEmpty() ? "is empty" : "found"));
        return friends;
    }

    @Override
    public List<User> findCommonFriends(int userId, int otherUserId) {
        String sqlQuery =
                "SELECT U.* FROM USERS U " +
                        "JOIN FRIENDS F1 ON U.USER_ID = F1.FRIEND_ID " +
                        "JOIN FRIENDS F2 ON U.USER_ID = F2.FRIEND_ID " +
                        "WHERE F1.USER_ID = ? AND F2.USER_ID = ?";
        List<User> commonFriends = jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherUserId);
        log.info("/GET. Common friends " + (commonFriends.isEmpty() ? "is empty" : "found"));
        return commonFriends;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(getFriends(rs.getInt("user_id")))
                .build();
    }

    private Set<Integer> getFriends(int userId) {
        String sqlQuery =
                "SELECT FRIEND_ID " +
                        "FROM FRIENDS " +
                        "WHERE USER_ID=?";

        List<Integer> friends =
                jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("friend_id"), userId);
        return new HashSet<>(friends);
    }

    private void setFriendshipStatus(int userId, int friendId) {
        String sqlQuery = "SELECT FRIENDSHIP_ID " +
                "FROM FRIENDS " +
                "WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";

        List<Integer> id = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("FRIENDSHIP_ID"),
                    userId, friendId, friendId, userId);

        if (!id.isEmpty()) {
            String insertQuery;
            if (id.size() == 2 && id.get(0) > 0) {
                String deleteQuery =
                        "DELETE " +
                                "FROM FRIENDS_STATUSES WHERE FRIENDSHIP_ID=?";
                jdbcTemplate.update(deleteQuery, id.get(0));
                insertQuery = "INSERT INTO FRIENDS_STATUSES (FRIENDSHIP_ID, FRIENDSHIP_STATUS) " +
                        "VALUES (?, 'CONFIRMED')";
                log.info("/PUT. Friendship status between {} and {} set to CONFIRMED", userId, friendId);
            } else {
                insertQuery = "INSERT INTO FRIENDS_STATUSES (FRIENDSHIP_ID, FRIENDSHIP_STATUS) " +
                        "VALUES (?, 'NOT_CONFIRMED')";
                log.info("/PUT. Friendship status between {} and {} set to NOT_CONFIRMED", userId, friendId);
            }
            jdbcTemplate.update(insertQuery, id.get(0));
        }
    }
}
