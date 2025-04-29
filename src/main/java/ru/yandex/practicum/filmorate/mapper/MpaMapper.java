package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

public final class MpaMapper {

    public static MpaDto mapToDto(Mpa mpa) {
        return  MpaDto.builder()
                .ratingId(mpa.getRatingId())
                .build();
    }
}
