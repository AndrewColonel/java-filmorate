package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.GenresDto;
import ru.yandex.practicum.filmorate.model.Genres;

public final class GenresMapper {
    public static GenresDto mapToGenresDto(Genres genres) {
        return GenresDto.builder()
                .id(genres.getId())
                .build();
    }

}
