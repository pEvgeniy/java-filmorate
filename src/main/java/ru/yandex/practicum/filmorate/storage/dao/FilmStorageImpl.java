package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class FilmStorageImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(insert.executeAndReturnKey(filmToMap(film)).intValue());
        setUpFilm(film);
        log.info("/POST. Film with id = {} created", film.getId());
        return film;
    }

    @Override
    public Film delete(Film film) {
        String sqlQuery =
                "DELETE " +
                        "FROM FILMS " +
                        "WHERE FILM_ID=? ";
        int result = jdbcTemplate.update(sqlQuery, film.getId());
        if (result == 0) {
            log.error("/DELETE. Film with id = {} to be deleted not found", film.getId());
            throw new EntityNotFoundException("Film with id = " + film.getId() + " not found");
        }
        log.info("/DELETE. Film with id = {} deleted", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery =
                "UPDATE films " +
                        "SET name=?, description=?, release_date=?, duration=?, mpa_id=? " +
                        "WHERE film_id=?";

        int result = jdbcTemplate.update(sqlQuery,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(),
                film.getId());
        if (result == 0) {
            log.error("/PUT. Film with id = {} to be updated not found", film.getId());
            throw new EntityNotFoundException("Film with id = " + film.getId() + " not found");
        }
        updateGenreIdToFilms(film);
        setUpFilm(film);
        log.info("/PUT. Film with id = {} updated", film.getId());
        return film;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery =
                "SELECT film_id, name, description, release_date, duration, mpa_id " +
                        "FROM films ";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        log.info("/GET. Films " + (films.isEmpty() ? "is empty" : "found"));
        return films;
    }

    @Override
    public Film findFilmById(int filmId) {
        String sqlQuery =
                "SELECT * " +
                        "FROM FILMS " +
                        "WHERE FILM_ID=?";

        try {
            log.info("/GET. Film with id = {} found", filmId);
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } catch (DataAccessException e) {
            log.error("/GET. Film with id = {} not found", filmId);
            throw new EntityNotFoundException("Film not found");
        }
    }

    private Film setUpFilm(Film film) {
        addGenreIdToFilms(film);
        getGenres(film);
        film.setMpa(getMpa(film.getMpa().getId()));
        return film;
    }

    private Film getGenres(Film film) {
        String sqlQuery =
                "SELECT GENRES.GENRE_ID, GENRES.NAME " +
                        "FROM GENRES JOIN GENRES_TO_FILMS GTF on GENRES.GENRE_ID = GTF.GENRE_ID " +
                        "WHERE FILM_ID=? " +
                        "GROUP BY GENRES.GENRE_ID, GENRES.NAME";

        jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Genre genre;
            do {
                genre = Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .name(rs.getString("name"))
                        .build();
                film.getGenres().remove(genre);
                film.getGenres().add(genre);
            } while (rs.next());
            return genre;
        }, film.getId());
        return film;
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }

    private void addGenreIdToFilms(Film film) {
        if (Objects.isNull(film.getGenres())) {
            return;
        }

        film.getGenres().forEach(genre -> {
            String sqlQuery = "INSERT INTO genres_to_films(film_id, genre_id) VALUES (?, ?)";
            int result = jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            if (result == 0) {
                log.error("/POST. Film to be updated not found");
                throw new EntityNotFoundException("Film with id = " + film.getId() + " not found");
            }
            log.info("/POST. Genre inserted into genres_to_films");
        });
    }

    private void updateGenreIdToFilms(Film film) {
        String sqlQuery =
                "DELETE " +
                        "FROM GENRES_TO_FILMS " +
                        "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getId());
        addGenreIdToFilms(film);
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
}
