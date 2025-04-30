package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreList;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class GenreListDbStorage extends BaseDbStorage<GenreList> {

    private static final String FIND_ALL_BENRELIST_QUERY = "SELECT gl.genre_list_id, gl.film_id, gl.genre_id, " +
            "g.genre_name  " +
            "FROM genre_list AS gl LEFT OUTER JOIN  genre AS g ON gl.genre_id = g.genre_id WHERE film_id = ?";
//    private static final String UPDATE_GENRELIST_QUERY = "UPDATE genre_list SET genre_id = ? WHERE film_id = ?";
    private static final String CREATE_GENRELIST_QUERY = "INSERT INTO genre_list (film_id, genre_id)" +
            "VALUES (?, ?)";
    private static final String DELETE_ALL_GENRELIST_ID_QUERY = "DELETE FROM genre_list WHERE film_id = ?";

    public GenreListDbStorage(JdbcTemplate jdbc, RowMapper<GenreList> mapper) {
        super(jdbc, mapper);
    }

    public Set<Genres> findAllFilmGenres(long id) {
        return findMany(FIND_ALL_BENRELIST_QUERY, id).stream()
                .map(genreList -> Genres.builder()
                        .id(genreList.getGenreId())
                        .name(genreList.getGenreName())
                        .build())
                .collect(Collectors.toSet());
    }

    public void addGenreList(Film film) {
        // объект film уже проверен на содержимое Genres
        film.getGenres().forEach(g -> insert(CREATE_GENRELIST_QUERY, film.getId(), g.getId()));

    }

    public void deleteGenreList(Film film) {
        // объект film уже проверен на содержимое Genres
        delete(DELETE_ALL_GENRELIST_ID_QUERY, film.getId());
    }

}
