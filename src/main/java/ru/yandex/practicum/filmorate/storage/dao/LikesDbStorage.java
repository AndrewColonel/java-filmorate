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

    private static final String FIND_ALL_LIKES_QUERY = "SELECT * FROM likes WHERE film_id = ?";
    private static final String CREATE_FRIENDS_ID_QUERY = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_FRIENDS_ID_QUERY = "DELETE FROM likes WHERE like_id = ?";


    public LikesDbStorage(JdbcTemplate jdbc, RowMapper<Likes> mapper) {
        super(jdbc, mapper);
    }

    // получаем множество уникальных id пользователей для избранного фильма
    public Set<Long> findAllLikes(long id) {
        log.debug("Получение списка всех лайков от  пользователя для фильма {}", id);
        return findMany(FIND_ALL_LIKES_QUERY, id).stream()
                .map(Likes::getUserId)
                .collect(Collectors.toSet());
    }

    // обновляем список лайков фильма - сначала зачищаем весь список лайков данного фильма
    // и вне зависимости от рзультата, заново  записываем ноые id пользователей поставивших лайки
//    public void updateLikes(long filmId, Set<Long> likes) {
//        delete(DELETE_FRIENDS_ID_QUERY,filmId);
//        likes.forEach(id -> insert(CREATE_FRIENDS_ID_QUERY, id, filmId));
//    }
    public void updateLikes(Film film) {

        // это "старый" списко лайков
        List<Likes> oldLikes = findMany(FIND_ALL_LIKES_QUERY, film.getId());

        // ассиметричная разница старого списка пользователей и нового - даст списко лайков на уделние
        List<Likes> LikesToDelete = oldLikes.stream()
                .filter(l -> !film.getLikes().contains(l.getUserId()))
                .toList();
        // удаляю лайки пользователей из таблицы
        LikesToDelete.forEach(l -> delete(DELETE_FRIENDS_ID_QUERY,l.getId()));

        // мноджество пользователей, которые ранее поставили лайки этому фильму
        Set<Long> oldLikesId = oldLikes.stream()
                .map(Likes::getUserId)
                .collect(Collectors.toSet());

        // ассиметричная разница множества пользователей - даст списко id на добавление
        Set<Long> idLikesToAdd = film.getLikes().stream()
                .filter(l -> !oldLikesId.contains(l))
                .collect(Collectors.toSet());

        // добавляю новые id в таблицу лайков
        idLikesToAdd.forEach(id -> insert(CREATE_FRIENDS_ID_QUERY, id, film.getId()));
    }

}
