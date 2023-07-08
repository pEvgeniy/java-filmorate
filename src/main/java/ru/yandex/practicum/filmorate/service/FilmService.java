package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    List<Film> findPopularFilms(int count);

}
