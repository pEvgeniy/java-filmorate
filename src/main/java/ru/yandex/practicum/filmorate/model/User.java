package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    @PositiveOrZero
    private int id;

    @NotBlank
    @Pattern(regexp = "\\S+", message = "User login must not contain whitespaces.")
    private String login;

    private String name;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}
