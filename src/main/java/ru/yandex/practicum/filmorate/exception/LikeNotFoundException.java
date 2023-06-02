package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

public class LikeNotFoundException extends RuntimeException {
    @Getter
    private final String parameter;

    public LikeNotFoundException(String parameter) {
        this.parameter = parameter;
    }
}
