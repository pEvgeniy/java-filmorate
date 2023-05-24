package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    protected final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @PostMapping
    public Film create(@RequestBody Film film) {
        checkFilmValidity(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Film {} added", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        checkFilmValidity(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Updated film {}", film.getName());
            return film;
        }
        log.warn("Film {} to update not found!", film.getName());
        throw new NoSuchElementException("Film to be updated not found!");
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Total films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    private void checkFilmValidity(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Film name is empty. {}", film);
            throw new InvalidFilmException("Film name must not be empty!");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            log.warn("Film description is empty. {}", film);
            throw new InvalidFilmException("Film description must not be empty!");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Film description is too long ({} signs, when 200 allowed). {}",
                    film.getDescription().length(), film);
            throw new InvalidFilmException("Film description must not be empty!");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Film duration is negative or zero. {}", film);
            throw new InvalidFilmException("Film duration must be positive!");
        }
        if (film.getReleaseDate() == null) {
            log.warn("Film release date is null. {}", film);
            throw new InvalidFilmException("Film release date must be not null!");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 11, 28))) {
            log.warn("Film release date is too old (should be newer than 28.11.1895). {}", film);
            throw new InvalidFilmException("Film release date must be not older than 28.11.1895!");
        }
    }
}
