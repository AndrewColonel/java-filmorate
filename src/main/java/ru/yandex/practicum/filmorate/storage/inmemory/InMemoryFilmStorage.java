package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, FilmDto> films = new HashMap<>();

    // получение всех фильмов
    @Override
    public Collection<Film> findAll() {
//        log.trace("Получение списка всех фильмов.");
//        return films.values();
        return null;
    }

    @Override
    public Collection<Film> findFilmTopChart(long count) {
        return null;

    }

    // поиск фильмов по ID
    @Override
    public Film findFilmDtoById(long id) {
//        FilmDto filmDto = films.get(id);
//        if (filmDto == null) throw new NotFoundException(String.format("Фильма с ID %d не найдено", id));
//        return filmDto;
        return  null;
    }

    @Override
    public Film findFilmById(long id) {
        return new Film(1L, "nisi eiusmod", 100,"adipisicing",
                LocalDate.parse("1967-03-25"), Set.of(),
                Set.of(),new Mpa(1, "htqnbyu"));
    }

    // добавление фильма
    @Override
    public FilmDto create(Film film) {
        log.trace("Начата обработка данных для создания нового фильма.");
        if (isNotValid(film)) {
            log.debug("фильм {} не прошел валидацию при создании", film);
            throw new ValidationException("Неверные данные о фильме");
        }
        film.setId(getNextId());
        // при создании c lombok контрсутором список остался null- необходимо создать пустой список
        if (film.getLikes() == null) film.setLikes(new HashSet<>());
        films.put(film.getId(), FilmMapper.mapToFilmDto(film));
        log.debug("Фильм {} добавлен в хранилище", film);
        return FilmMapper.mapToFilmDto(film);
    }

    // обновление фильма
    @Override
    public FilmDto update(Film newFilm) {
        log.trace("Начата обработка данных для Обновления информации об имеющемся фильме.");
        if (newFilm.getId() == null) {
            log.error("не указан ID при обновлении для фильма {}", newFilm);
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            if (isNotValid(newFilm)) {
                log.debug("фильм {} не прошел валидацию при обновлении", newFilm);
                throw new ValidationException("Неверные данные о фильме");
            }
            FilmDto oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            // если передали список лайков, надо его принять
            if (newFilm.getLikes() != null) {
                oldFilm.setLikes(newFilm.getLikes());
            }
            log.debug("Фильм {} обновлен в хранилище", oldFilm);
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public  void addLikes(long filmId, long userId) {

    }

    public void delLikes(long filmId, long userId) {

    }

    // вспомогательный метод валидации экземпляра фильма
    private boolean isNotValid(Film film) {
        return (film.getDescription() != null)
                // максимальная длина описания — 200 символов;
                && film.getDescription().length() > 200
                // дата релиза — не раньше 28 декабря 1895 года;
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28));
    }

    // вспомогательный метод получения следующего значения id
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
