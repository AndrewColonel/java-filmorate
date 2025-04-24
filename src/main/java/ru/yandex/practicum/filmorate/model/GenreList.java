package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GenreList {

    Long id;
    Long filmId; // идентификатор фильма
    Long genreId; // идентификатор жанра фильма

}
