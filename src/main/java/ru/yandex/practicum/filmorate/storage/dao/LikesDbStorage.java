package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Likes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class LikesDbStorage extends BaseDbStorage<Likes> {

    private static final String FIND_ALL_FILM_LIKES_QUERY = "SELECT * FROM likes WHERE film_id = ?";
    private static final String FIND_ALL_LIKES_QUERY = "SELECT * FROM likes";
    private static final String CREATE_LIKES_ID_QUERY = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_LIKES_ID_QUERY = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String DELETE_ALL_LIKES_ID_QUERY = "DELETE FROM likes WHERE film_id = ?";

    public LikesDbStorage(JdbcTemplate jdbc, RowMapper<Likes> mapper) {
        super(jdbc, mapper);
    }

    // получаем множество уникальных id пользователей для избранного фильма
    public Set<Long> findFilmAllLikes(long id) {
        log.debug("Получение списка всех лайков от  пользователя для фильма {}", id);
        return findMany(FIND_ALL_FILM_LIKES_QUERY, id).stream()
                .map(Likes::getUserId)
                .collect(Collectors.toSet());
    }

    public List<Likes> findAllLikes() {
        log.debug("выполняется запрос на получение всех лайков для всех фильмов");
        return findMany(FIND_ALL_LIKES_QUERY);
    }

    public void addLikes(long filmId, long userId) {
        log.debug("добавляем лайк пользователя {} , для фильма {}",userId,filmId);
        insert(CREATE_LIKES_ID_QUERY, userId, filmId);
    }

    public void delLikes(long filmId, long userId) {
        log.debug("удаляется лайк пользователя {} , для фильма {}",userId,filmId);
        delete(DELETE_LIKES_ID_QUERY, userId, filmId);
    }

    // обновляем список лайков фильма - сначала зачищаем весь список лайков данного фильма
    // и вне зависимости от рзультата, заново  записываем ноые id пользователей поставивших лайки
    public void updateLikes(Film film) {

        log.trace("обновление лайков для фильма {}", film.getId());
        // это "старый" списко лайков
        List<Likes> oldLikes = findMany(FIND_ALL_FILM_LIKES_QUERY, film.getId());

        // мноджество пользователей, которые ранее поставили лайки этому фильму
        Set<Long> oldLikesId = oldLikes.stream()
                .map(Likes::getUserId)
                .collect(Collectors.toSet());
        // ассиметричная разница старого списка пользователей и нового - даст списко лайков на уделние
        Set<Long> idLikesToDelete = oldLikesId.stream()
                .filter(e -> !film.getLikes().contains(e))
                .collect(Collectors.toSet());

        // удаляю лайки пользователей из таблицы
        idLikesToDelete.forEach(e -> delete(DELETE_LIKES_ID_QUERY, e, film.getId()));

        // ассиметричная разница множества пользователей - даст списко id на добавление
        Set<Long> idLikesToAdd = film.getLikes().stream()
                .filter(l -> !oldLikesId.contains(l))
                .collect(Collectors.toSet());

        // добавляю новые id в таблицу лайков
        idLikesToAdd.forEach(id -> insert(CREATE_LIKES_ID_QUERY, id, film.getId()));
    }


    public void deleteAllLikes(Film film) {
        delete(DELETE_ALL_LIKES_ID_QUERY, film.getId());
    }
}
