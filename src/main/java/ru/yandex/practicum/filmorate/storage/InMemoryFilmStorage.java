package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    @Getter
    protected final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @Override
    public Film create(Film film) {
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Film {} added", film.getName());
        return film;
    }

    @Override
    public Film delete(Film film) {
        Film foundFilm = films.get(film.getId());
        if (foundFilm != null) {
            films.remove(film.getId());
            log.info("Film {} removed", film.getName());
            return film;
        }
        log.warn("Film {} to update not found", film.getName());
        throw new FilmNotFoundException("Film not found");
    }

    @Override
    public Film update(Film film) {
        return films.keySet().stream()
                .filter(f -> f.equals(film.getId()))
                .findFirst()
                .map(f -> {
                    films.put(film.getId(), film);
                    log.info("Updated film {}", film.getName());
                    return film;
                })
                .orElseThrow(() -> {
                    log.warn("Film {} to update not found", film.getName());
                    return new FilmNotFoundException("Film not found");
                });
    }

    @Override
    public List<Film> findAll() {
        log.info("Total films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(int filmId) {
        Film foundFilm = films.get(filmId);
        if (foundFilm != null) {
            log.info("Found by id film {}", foundFilm);
            return foundFilm;
        }
        log.warn("Film by id={} not found", filmId);
        throw new FilmNotFoundException("Film not found");
    }
}
