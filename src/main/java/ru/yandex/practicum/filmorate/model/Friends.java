package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Friends {

    Long id;
    Long userId; // идентификатор первого пользователя
    Long friendId; // идентификатор второго пользователя

}
