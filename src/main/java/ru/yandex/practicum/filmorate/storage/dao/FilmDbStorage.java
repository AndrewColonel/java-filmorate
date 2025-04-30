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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<FilmRequest> implements FilmStorage {

//    private static final String FIND_ALL_QUERY = "SELECT  film_id, name, duration, description, release_date, " +
//            "rating_name  FROM films AS f LEFT OUTER JOIN rating AS r ON f.rating_id = r.rating_id";

    private static final String FIND_ALL_QUERY = "SELECT *  FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT *  FROM films WHERE film_id = ?";

//    private static final String CREATE_QUERY = "INSERT INTO films " +
//            "(name, duration, description, release_date, rating_id)" +
//            "VALUES (?, ?, ?, ?, ?)";

    private static final String CREATE_QUERY = "INSERT INTO films " +
            "(name, duration, description, release_date)" +
            "VALUES (?, ?, ?, ?)";


    //    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, duration = ?, description = ? , " +
//            "release_date = ?, rating_id = ?  WHERE film_id = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, duration = ?, description = ? , " +
            "release_date = ? WHERE film_id = ?";

    private static final String UPDATE_RATING_MPA_QUERY = "UPDATE films SET rating_id = ?" +
            "WHERE film_id = ?";


//    private static final String CREATE_GENRE_LIST = "INSERT INTO genre_list (film_id, genre_id)" +
//            "VALUES (?, ?)";


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
        log.debug("Разбираем НОВЫЙ фильм {} ", film);
        if (isNotValid(film)) {
            log.debug("фильм {} не прошел валидацию при создании", film);
            throw new ValidationException("Неверные данные о фильме");
        }
        log.trace("Начато создания нового фильма.");
//        long id = insert(CREATE_QUERY,
//                film.getName(),
//                film.getDuration(),
//                film.getDescription(),
//                film.getReleaseDate(),
//                film.getMpa().getId());

        long id = insert(CREATE_QUERY,
                film.getName(),
                film.getDuration(),
                film.getDescription(),
                film.getReleaseDate());
        film.setId(id);
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
            log.debug("MPA фильма {}",film.getMpa().getId());
            update(UPDATE_RATING_MPA_QUERY, film.getMpa().getId(), id);
        }

        log.trace("Передаем фильму список Жанров, если все верно.");
        if (Objects.nonNull(film.getGenres())) {
            // проверяем что внутри списка
//            film.getGenres().stream()
//                    .peek(System.out::println)
//                    .filter(genres -> genres.getId() <= 0
//                            || genres.getId() > 6)
//                    .findFirst().orElseThrow(() ->
//                            new NotFoundException("Жанр не существует"));

            if (film.getGenres().stream()
                    .peek(genres -> log.debug("Жанр {}",genres))
                    .anyMatch(genres -> genres.getId() <= 0
                    || genres.getId() > 6)) {
                throw new NotFoundException("Жанр не существует");
            }


//            film.setGenres(film.getGenres().stream()
//                    .filter(genres -> genres.getId() != 0)
//                    .collect(Collectors.toSet()));

            genreListDbStorage.addGenreList(film);
        } else {
            film.setGenres(Set.of());
        }
        log.debug("Фильм {} добавлен в хранилище", film);
        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public FilmDto update(Film film) {
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

//        update(UPDATE_QUERY,
//                film.getName(),
//                film.getDuration(),
//                film.getDescription(),
//                film.getReleaseDate(),
//                film.getMpa().getId(),
//                film.getId());

        update(UPDATE_QUERY,
                film.getName(),
                film.getDuration(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getId());



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
            log.debug("MPA обновленному фильма {}",film.getMpa().getId());
            update(UPDATE_RATING_MPA_QUERY, film.getMpa().getId(), film.getId());
        }

        log.trace("Передаем обновленному фильму список Жанров, если все верно.");
        if (Objects.nonNull(film.getGenres())) {
            // проверяем что внутри списка
//            film.getGenres().stream()
//                    .filter(genres -> genres.getId() <= 0
//                            || genres.getId() > 6)
//                    .findFirst().orElseThrow(() ->
//                            new NotFoundException("Жанр не существует"));

            if (film.getGenres().stream()
                    .peek(genres -> log.debug("Жанр {}",genres))
                    .anyMatch(genres -> genres.getId() <= 0
                            || genres.getId() > 6)) {
                throw new NotFoundException("Жанр не существует");
            }





// если с обновлением пришли новые жанры, удаляю старые, их немного
            genreListDbStorage.deleteGenreList(film);
//            film.setGenres(film.getGenres().stream()
//                    .filter(genres -> genres.getId() != 0)
//                    .collect(Collectors.toSet()));

            genreListDbStorage.addGenreList(film);
        } else {
            genreListDbStorage.deleteGenreList(film);
            film.setGenres(Set.of());
        }


//        if (Objects.nonNull(film.getGenres())) {
//            // если с обновлением пришли новые жанры, удаляю старые, их немного
//            genreListDbStorage.deleteGenreList(film);
//            // проверяем что внутри списка нет жанров с id = 0
//            film.setGenres(film.getGenres().stream()
//                    .filter(genres -> genres.getId() != 0)
//                    .collect(Collectors.toSet()));
//            // добавляем новые жанры
//            genreListDbStorage.addGenreList(film);
//        } else {
//            genreListDbStorage.deleteGenreList(film);
//            film.setGenres(Set.of());
//        }


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
