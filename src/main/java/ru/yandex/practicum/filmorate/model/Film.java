package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class Film {
    Long id;

    // название не может быть пустым и null
    @NotBlank
    String name;

    // продолжительность фильма должна быть положительным числом и не null
    @Positive
    @NotNull
    Integer duration;

    // описание может быть null, но если есть то не более 200 символов,
    // проверка валидности в контроллере
    String description;

    // дата выпуска должна быть не null,
    // проверка на валидность самой даты выполняется в контроллере
    @NotNull
    LocalDate releaseDate;

    // свойство likes в классе фильмы,
    // будет содержать список уникальных id пользователей поставивших лайк
    Set<Long> likes;

    // свойство genres будет содержать список идентификаторов жанров
    Set<Genres> genres;

    // идентификатор рейтинга фильмов
    Mpa mpa;
}


