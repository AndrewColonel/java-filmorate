// класс для обработки данных из БД

package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@Builder
public class FilmRequest {
    Long id;
    String name;
    Integer duration;
    String description;
    LocalDate releaseDate;
    int ratingId;
}