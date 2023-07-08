package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.IsAfter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Film {
    @PositiveOrZero
    private int id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200, message = "Description length must be less than 200 characters.")
    private String description;

    @NotNull
    @IsAfter(year = 1895, month = 11, day = 28)
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private Long duration;

    private Set<Genre> genres;

    @NotNull
    private Mpa mpa;
}
