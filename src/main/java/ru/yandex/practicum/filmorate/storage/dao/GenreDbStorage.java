package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.GenreType;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery =
                "SELECT GENRE_ID, NAME " +
                        "FROM GENRES";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::mapToRowGenre);
        log.info("/GET. Genres " + (genres.isEmpty() ? "is empty" : "found"));
        return genres;
    }

    @Override
    public Genre findGenreById(int id) {
        String sqlQuery =
                "SELECT GENRE_ID, NAME " +
                        "FROM GENRES " +
                        "WHERE GENRE_ID=?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sqlQuery, this::mapToRowGenre, id);
            log.info("/GET. Found genre with id = {}", id);
            return genre;
        } catch (DataAccessException e) {
            log.error("/GET. Genre with id = {} not found", id);
            throw new GenreNotFoundException("Genre with id = " + id + " not found");
        }
    }

    private Genre mapToRowGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(GenreType.valueOf(rs.getString("name")))
                .build();
    }
}
