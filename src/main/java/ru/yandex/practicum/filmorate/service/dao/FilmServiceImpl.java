package ru.yandex.practicum.filmorate.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    public FilmServiceImpl(FilmStorage filmStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film addLike(int filmId, int userId) {
        String sqlQuery =
                "INSERT INTO LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
        int result = jdbcTemplate.update(sqlQuery, userId, filmId);
        if (result == 0) {
            log.error("/PUT. Film(id = {}) or user(id = {}) not found", filmId, userId);
            throw new EntityNotFoundException("Error with insert into LIKES (USER_ID, FILM_ID)");
        }
        log.info("/PUT. Like added");
        return filmStorage.findFilmById(filmId);
    }

    public Film deleteLike(int filmId, int userId) {
        String sqlQuery =
                "DELETE " +
                        "FROM LIKES " +
                        "WHERE USER_ID=? AND FILM_ID=?";
        int result = jdbcTemplate.update(sqlQuery, userId, filmId);
        if (result == 0) {
            log.error("/DELETE. Film(id = {}) or user(id = {}) to be deleted not found", filmId, userId);
            throw new EntityNotFoundException("Like not found");
        }
        log.info("/DELETE. Friend deleted");
        return filmStorage.findFilmById(filmId);
    }

    public List<Film> findPopularFilms(int count) {
        String sql = "SELECT f.* " +
                "FROM films f " +
                "JOIN (SELECT film_id, COUNT(*) AS like_count " +
                "FROM likes " +
                "GROUP BY film_id " +
                "ORDER BY like_count DESC " +
                "LIMIT ?) l " +
                "ON f.film_id = l.film_id";

        List<Film> popularFilms = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        if (popularFilms.size() == 0) {
            popularFilms = findAny(count);
        }
        log.info("/GET. Friends found");
        return popularFilms;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpa(getMpa(rs.getInt("mpa_id")))
                .genres(getGenres(rs.getInt("film_id")))
                .build();
    }

    private Mpa getMpa(int id) {
        String sqlQuery =
                "SELECT mpa_id, name " +
                        "FROM mpa_type " +
                        "WHERE mpa_id=?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("name"))
                .build();
    }

    private Set<Genre> getGenres(int id) {
        Set<Genre> genres = new LinkedHashSet<>();

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(
                "SELECT GENRES.GENRE_ID, GENRES.NAME " +
                        "FROM GENRES JOIN GENRES_TO_FILMS GTF on GENRES.GENRE_ID = GTF.GENRE_ID " +
                        "WHERE FILM_ID=? " +
                        "GROUP BY GENRES.GENRE_ID, GENRES.NAME",
                id);

        while (sqlRowSet.next()) {
            genres.add(Genre.builder()
                    .id(sqlRowSet.getInt("genre_id"))
                    .name(sqlRowSet.getString("name"))
                    .build());
        }
        return genres;
    }

    private List<Film> findAny(int count) {
        String sqlQuery =
                "SELECT *" +
                        "FROM FILMS " +
                        "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        if (films.size() == 0) {
            log.info("/GET. Friends is empty");
            throw new EntityNotFoundException("Films not found");
        }
        return films;
    }
}
