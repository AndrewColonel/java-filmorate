package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRequest;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<FilmRequest> implements FilmStorage {

//    private static final String FIND_ALL_QUERY = "SELECT  film_id, name, duration, description, release_date, " +
//            "rating_name  FROM films AS f LEFT OUTER JOIN rating AS r ON f.rating_id = r.rating_id";

    private static final String FIND_ALL_QUERY = "SELECT *  FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT *  FROM films WHERE film_id = ?";
    private static final String CREATE_QUERY = "INSERT INTO films " +
            "(name, duration, description, release_date, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String CREATE_GENRE_LIST = "INSERT INTO genre_list (film_id, genre_id)" +
            "VALUES (?, ?)";

    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, duration = ?, description = ? , " +
            "release_date = ?, rating_id = ? WHERE film_id = ?";

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
    public List<FilmDto> findAll() {

        return findMany(FIND_ALL_QUERY).stream()
                // используя поток FilmRequest и содержащий rating_id и нахожу MPA, преобразую поток в Film
                .map(filmRequest ->
                        FilmMapper.mapToFilm(filmRequest, mpaDbStorage.findMpaById(filmRequest.getRatingId())))
                // добавляем Likes по id фильма
                .peek(film -> film.setLikes(likesDbStorage.findAllLikes(film.getId())))
                //добавялем Genres по ID фильма
                .peek(film -> film.setGenres(genreListDbStorage.findAllFilmGenres(film.getId())))
                // преобразуб поток в FilmDto
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    @Override
    public FilmDto findFilmById(long id) {
        FilmRequest filmRequest = findOne(FIND_BY_ID_QUERY, id).orElseThrow(
                () -> new NotFoundException(String.format("Фильма с ID %d не найдено", id))
        );
        Film film = FilmMapper.mapToFilm(filmRequest, mpaDbStorage.findMpaById(filmRequest.getRatingId()));
        film.setLikes(likesDbStorage.findAllLikes(id));
        film.setGenres(genreListDbStorage.findAllFilmGenres(film.getId()));
        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public FilmDto create(Film film) {
        log.trace("Начата обработка данных для создания нового фильма.");
        if (isNotValid(film)) {
            log.debug("фильм {} не прошел валидацию при создании", film);
            throw new ValidationException("Неверные данные о фильме");
        }
        long id = insert(CREATE_QUERY,
                film.getName(),
                film.getDuration(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getMpa().getRatingId());
//                film.getMpa().stream().findFirst().map(Mpa::getRatingId));
        film.setId(id);

//        if (film.getMpa() != null) mpaDbStorage.create(film.getMpa().getRatingId()
//                ,film.getMpa().getRatingName());


        //TODO
//        film.getGenres().forEach(genreId ->
//                insert(CREATE_GENRE_LIST, id, genreId));

//        if (film.getMpa() == null) film.setMpa(new Mpa(0,null));
//        if (film.getGenres() == null) film.setGenres(Set.of());
        // при создании фильма список лайки никто не ставил - необходимо создать пустой список
        if (film.getLikes() == null) film.setLikes(new HashSet<>());

        if (film.getGenres() != null) {
                genreListDbStorage.addGenreList(film);
        }


        log.debug("Фильм {} добавлен в хранилище", film);
        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public FilmDto update(Film film) {
        log.trace("Начата обработка данных для Обновления информации об имеющемся фильме.");
        if (film.getId() == null) {
            log.error("не указан ID при обновлении для фильма {}", film);
            throw new ValidationException("Id должен быть указан");
        }
        if (isNotValid(film)) {
            log.debug("фильм {} не прошел валидацию при обновлении", film);
            throw new ValidationException("Неверные данные о фильме");
        }
        update(UPDATE_QUERY,
                film.getName(),
                film.getDuration(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getMpa().getRatingId(),
//                film.getMpa().stream().findFirst().map(Mpa::getRatingId),
                film.getId());
        // если передали список лайков, надо его принять
        if (film.getLikes() != null) {
//              film.getLikes().forEach(filmid ->
//                      likesDbStorage.updateLikes(film.getId(),film.getLikes()));
            likesDbStorage.updateLikes(film);
        }


        log.debug("Фильм {} обновлен в хранилище", film);
        return FilmMapper.mapToFilmDto(film);


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
