package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    // описание может быть null, но если есть то не более 200 символов,
    // проверка валидности в контроллере
    String description;

    // дата выпуска должна быть не null,
    // проверка на валидность самой даты выполняется в контроллере
    @NotNull
    LocalDate releaseDate;

    // продолжительность фильма должна быть положительным числом и не null
    @Positive
    @NotNull
    Integer duration;
}


