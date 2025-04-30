// класс для преобразования объекта, полученного из БД -  FilmRequest в объект Film
package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenresDto;
import ru.yandex.practicum.filmorate.model.FilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class FilmMapper {


    public static Film mapToFilm(FilmRequest filmRequest, Mpa mpa) {
        return Film.builder()
                .id(filmRequest.getId())
                .name(filmRequest.getName())
                .duration(filmRequest.getDuration())
                .description(filmRequest.getDescription())
                .releaseDate(filmRequest.getReleaseDate())
                .mpa(mpa)
                .build();
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .duration(film.getDuration())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .likes(film.getLikes())
                .genres(film.getGenres().stream()

//                        .filter(Objects::nonNull)

                        .map(GenresMapper::mapToGenresDto)
                        .sorted(Comparator.comparingInt(GenresDto::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
            .mpa((MpaMapper.mapToDto(film.getMpa())))
                .build();


//         if (film.getMpa().getId() == 0) filmDto.setMpa(Set.of());

         return filmDto;
    }


}
