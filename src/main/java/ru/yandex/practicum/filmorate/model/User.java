package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {

    private int id;

    @NotBlank
    @Pattern(regexp = "\\S+", message = "User login must not contain whitespaces.")
    private String login;

    private String name;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Past
    private LocalDate birthday;

    private Set<Integer> friends;
}
