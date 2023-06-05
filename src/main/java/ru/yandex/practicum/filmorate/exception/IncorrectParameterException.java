package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

public class IncorrectParameterException extends RuntimeException {
    @Getter
    private final String parameter;

    public IncorrectParameterException(String parameter) {
        this.parameter = parameter;
    }
}
