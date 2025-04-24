package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Likes {
    Long id;
    Long userId; // идентификатор пользователя
    Long filmId; // идентификатор фильма

}
