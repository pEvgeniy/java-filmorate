package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = "name")
public class Genre {
    @NotNull
    @Positive
    private int id;

    @NotNull
    private String name;
}
