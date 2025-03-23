package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    Integer id;

    // название не может быть пустым и null
    @NotBlank
    String name;

    String description;
    LocalDate releaseDate;

    // продолжительность фильма должна быть положительным числом.
    @Positive
    Integer duration;
}


