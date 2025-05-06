package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Slf4j
@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<FilmRequest> implements FilmStorage {

    private static final String FIND_ALL_QUERY = "SELECT *  FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT *  FROM films WHERE film_id = ?";
    private static final String FIND_TOP_CHART_FILMS = "SELECT f.film_id, f.name, f.duration, f.description, " +
            "f.release_date, f.rating_id FROM films AS f LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";
    private static final String CREATE_QUERY = "INSERT INTO films " +
            "(rating_id, name, duration, description, release_date)" +
            "VALUES (1, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, duration = ?, description = ? , " +
            "release_date = ? WHERE film_id = ?";

    private static final String UPDATE_RATING_MPA_QUERY = "UPDATE films SET rating_id = ?" +
            "WHERE film_id = ?";

    private final LikesDbStorage likesDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreListDbStorage genreListDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<FilmRequest> mapper,
                         LikesDbStorage likesDbStorage, MpaDbStorage mpaDbStorage, GenreListDbStorage genreListDbStorage) {
        super(jdbc, mapper);
        this.likesDbStorage = likesDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreListDbStorage = genreListDbStorage;
    }

    @Override
    public List<Film> findAll() {
        return findAllFilms(FIND_ALL_QUERY);
    }

    // вспомогательный метод для получения списка всех фильмоы
    public List<Film> findAllFilms(String query,  Object... params) {
//        Заранее вытягиваю из БД все данные по жанрам (лайкам и MPA) и фильмам, собираю
//        мапу типа Map<film.getId(), Set > а далее - жанры и лайкамииз этой мапы.

        // собираю мапу с ключами -ID фильма и значением - множество лайков
        Map<Long, Set<Long>> likes = likesDbStorage.findAllLikes().stream()
                .collect(groupingBy(Likes::getFilmId,
                        mapping(Likes::getUserId, toSet())));

        // собираю мапу с ключами -ID фильма и значением - множество жанров
        Map<Long, Set<Genres>> genres = genreListDbStorage.findAllGenreList().stream()
                .collect(groupingBy(GenreList::getFilmId,
                        mapping(genreList ->
                                        Genres.builder()
                                                .id(genreList.getGenreId())
                                                .name(genreList.getGenreName())
                                                .build(),
                                toSet())));

        // собираю мапу с ключами -ID MPA и значением - модель MPA
        Map<Integer, Mpa> mpa = mpaDbStorage.findAll().stream()
                .collect(toMap(Mpa::getId, Function.identity(),
                        (existing, replacement) -> existing));

        return findMany(query,params).stream()
                .map(filmRequest ->
                        FilmMapper.mapToFilm(filmRequest, mpa.get(filmRequest.getRatingId())))
                .peek(film -> film.setLikes(likes.getOrDefault(film.getId(), Set.of())))
                .peek(film -> film.setGenres(genres.getOrDefault(film.getId(), Set.of())))
                .toList();
    }

    @Override
   // метод для получения списка фильмов, отсортированного по убыванию популярности (количества лайков)
    public Collection<Film> findFilmTopChart(long count) {
        return findAllFilms(FIND_TOP_CHART_FILMS,count);

    }

    @Override
    public Film findFilmDtoById(long id) {
        FilmRequest filmRequest = findOne(FIND_BY_ID_QUERY, id).orElseThrow(
                () -> new NotFoundException(String.format("Фильма с ID %d не найдено", id))
        );
        Film film = FilmMapper.mapToFilm(filmRequest, mpaDbStorage.findMpaById(filmRequest.getRatingId()));
        film.setLikes(likesDbStorage.findFilmAllLikes(id));
        film.setGenres(genreListDbStorage.findAllFilmGenres(film.getId()));
        return film;
    }


    @Override
    public Film findFilmById(long id) {
        FilmRequest filmRequest = findOne(FIND_BY_ID_QUERY, id).orElseThrow(
                () -> new NotFoundException(String.format("Фильма с ID %d не найдено", id))
        );
        Film film = FilmMapper.mapToFilm(filmRequest, mpaDbStorage.findMpaById(filmRequest.getRatingId()));
        film.setLikes(likesDbStorage.findFilmAllLikes(id));
        film.setGenres(
                genreListDbStorage.findAllFilmGenres(film.getId()).stream()
                        .sorted(Comparator.comparingInt(Genres::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        return film;
    }


    @Override
    public Film create(Film film) {
        log.trace("Начата обработка данных для создания нового фильма.");
        log.debug("Разбираем НОВЫЙ фильм {} ", film);
        if (isNotValid(film)) {
            log.debug("фильм {} не прошел валидацию при создании", film);
            throw new ValidationException("Неверные данные о фильме");
        }
        log.trace("Начато создания нового фильма.");

        long id = insert(CREATE_QUERY,
                film.getName(),
                film.getDuration(),
                film.getDescription(),
                film.getReleaseDate());
        film.setId(id);

        log.debug("Создана запись фильм {}", film);

        log.trace("Присваиваем фильму лайки, если есть ");
        if (Objects.nonNull(film.getLikes())) {
            likesDbStorage.updateLikes(film);
        } else {
            film.setLikes(new HashSet<>());
        }

        log.trace("присваиваем фильму MPA, если все верно.");
        if (Objects.nonNull(film.getMpa())) {
            if ((film.getMpa().getId() <= 0) || (film.getMpa().getId() > 5)) {
                throw new NotFoundException(String.format("MPA с ID %d не найдено", film.getMpa().getId()));
            }
            log.debug("MPA фильма {}", film.getMpa().getId());
            update(UPDATE_RATING_MPA_QUERY, film.getMpa().getId(), id);
        }

        log.trace("Передаем фильму список Жанров, если все верно.");
        if (Objects.nonNull(film.getGenres())) {

            if (film.getGenres().stream()
                    .peek(genres -> log.debug("Жанр {}", genres))
                    .anyMatch(genres -> genres.getId() <= 0
                            || genres.getId() > 6)) {
                throw new NotFoundException("Жанр не существует");
            }

            genreListDbStorage.addGenreList(film);
        } else {
            film.setGenres(Set.of());
        }
        log.debug("Фильм {} добавлен в хранилище", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.trace("Начата обработка данных для Обновления информации об имеющемся фильме.");
        log.debug("Разбираем ОБНОВЛЕНИЕ фильм {} ", film);
        if (film.getId() == null) {
            log.error("не указан ID при обновлении для фильма {}", film);
            throw new ValidationException("Id должен быть указан");
        }
        if (isNotValid(film)) {
            log.debug("фильм {} не прошел валидацию при обновлении", film);
            throw new ValidationException("Неверные данные о фильме");
        }
        log.trace("Начато обновление фильма.");

        update(UPDATE_QUERY,
                film.getName(),
                film.getDuration(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getId());

        log.debug("Обновлена запись фильм {}", film);

        log.trace("Присваиваем обновленному фильму лайки, если есть ");
        // если передали список лайков, надо его принять
        if (Objects.nonNull(film.getLikes())) {
            likesDbStorage.updateLikes(film);
        } else {
            likesDbStorage.deleteAllLikes(film);
            film.setLikes(new HashSet<>());
        }

        log.trace("присваиваем обновленному фильму MPA, если все верно.");
        if (Objects.nonNull(film.getMpa())) {
            if ((film.getMpa().getId() <= 0) || (film.getMpa().getId() > 5)) {
                throw new NotFoundException(String.format("MPA с ID %d не найдено", film.getMpa().getId()));
            }
            log.debug("MPA обновленному фильма {}", film.getMpa().getId());
            update(UPDATE_RATING_MPA_QUERY, film.getMpa().getId(), film.getId());
        }

        log.trace("Передаем обновленному фильму список Жанров, если все верно.");
        if (Objects.nonNull(film.getGenres())) {

            if (film.getGenres().stream()
                    .peek(genres -> log.debug("Жанр {}", genres))
                    .anyMatch(genres -> genres.getId() <= 0
                            || genres.getId() > 6)) {
                throw new NotFoundException("Жанр не существует");
            }

// если с обновлением пришли новые жанры, удаляю старые, их немного
            genreListDbStorage.deleteGenreList(film);
            genreListDbStorage.addGenreList(film);
        } else {
            genreListDbStorage.deleteGenreList(film);
            film.setGenres(Set.of());
        }

        log.debug("Фильм {} обновлен в хранилище", film);
        return film;
    }

    public void addLikes(long filmId, long userId) {
        likesDbStorage.addLikes(filmId, userId);
    }

    public void delLikes(long filmId, long userId) {
        likesDbStorage.delLikes(filmId, userId);
    }

    // вспомогательный метод валидации экземпляра фильма
    private boolean isNotValid(Film film) {
        return (film.getDescription() != null)
                // максимальная длина описания — 200 символов;
                && film.getDescription().length() > 200
                // дата релиза — не раньше 28 декабря 1895 года;
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28));
    }
}
