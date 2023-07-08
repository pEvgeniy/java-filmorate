package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class MpaStorageImpl implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        String sqlQuery =
                "SELECT MPA_ID, NAME " +
                        "FROM MPA_TYPE";
        List<Mpa> mpa = jdbcTemplate.query(sqlQuery, this::mapToRowMpa);
        log.info("/GET. Mpa " + (mpa.isEmpty() ? "is empty" : "found"));
        return mpa;
    }

    @Override
    public Mpa findMpaById(int id) {
        String sqlQuery =
                "SELECT MPA_ID, NAME " +
                        "FROM MPA_TYPE " +
                        "WHERE MPA_ID=?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapToRowMpa, id);
            log.info("/GET. Found mpa with id = {}", id);
            return mpa;
        } catch (DataAccessException e) {
            log.error("/GET. Mpa with id = {} not found", id);
            throw new EntityNotFoundException("Mpa with id = " + id + " not found");
        }
    }

    private Mpa mapToRowMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("name"))
                .build();
    }
}
