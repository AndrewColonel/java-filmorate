package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class FilmDto {
    Long id;
    String name;
    Integer duration;
    String description;
    LocalDate releaseDate;
    Set<Long> likes;
    Set<GenresDto> genres;
    MpaDto mpa;
}
