package ru.yandex.practicum.filmorate.dto;
// класс для передачи ID рейтинга MPA


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MpaDto {
    int id;
}
