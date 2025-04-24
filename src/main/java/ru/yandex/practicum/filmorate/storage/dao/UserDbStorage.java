package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;


@Slf4j
@Repository
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String CREATE_QUERY = "INSERT INTO users (login, email, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET login = ?, email = ?, name = ? , birthday = ?" +
            " WHERE user_id = ?";
    private final FriendsDbStorage friendsDbStorage;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, FriendsDbStorage friendsDbStorage) {
        super(jdbc, mapper);
        this.friendsDbStorage = friendsDbStorage;
    }

    @Override
    public List<User> findAll() {
        log.trace("Получение списка всех пользователей");
        // возвращаем списко всех пользователей и добавляем в поле объекта user список друзей
        return findMany(FIND_ALL_QUERY).stream()
                .peek(user -> user.setFriends(friendsDbStorage.findAlLfriends(user.getId())))
                .toList();
    }

    @Override
    public User findUserById(long id) {
     // получили объект пользователь из базы и добавили списко друзей
     User user =  findOne(FIND_BY_ID_QUERY, id).orElseThrow(
             () -> new NotFoundException(String.format("Пользователя с ID %d не существует.", id)));

     user.setFriends(friendsDbStorage.findAlLfriends(id));
     return user;
    }

    @Override
    public User create(User user) {
        log.trace("Начата обработка данных для создания нового пользователя");
        if (isNotValid(user)) {
            log.debug("Пользователь {} не прошел валидацию при создании", user);
            throw new ValidationException("Неверные данные о пользователе");
        }
        // если у пользователя не передано имя, меняем его на логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else {
            user.setName(user.getName());
        }
        long id = insert(CREATE_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday());
        user.setId(id);
        // созданный пользователь друзей пока не имеет- необходимо создать пустой список
        if (user.getFriends() == null) user.setFriends(new HashSet<>());
        log.debug("Пользователь {} добавлен в хранилище", user);
        return user;
    }

    @Override
    public User update(User user) {
        log.trace("Начата обработка данных для обновления информации об имеющемся пользователе");
        if (user.getId() == null) {
            log.error("не указан ID при обновлении для пользователя {}", user);
            throw new ValidationException("Id должен быть указан");
        }

        if (isNotValid(user)) {
            log.debug("Пользователь {} не прошел валидацию при обновлении", user);
            throw new ValidationException("Неверные данные о пользователе");
        }
        // если у пользователя не передано имя, меняем его на логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else {
            user.setName(user.getName());
        }

//        long rowUpdated =
                update(UPDATE_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId());
//        if (rowUpdated == 0) throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");

        if (user.getFriends() != null) {
            user.getFriends().forEach(friendId ->
                    friendsDbStorage.updateFriends(user.getId(), user.getFriends()));
        }

        log.debug("Пользователь {} обновлен в хранилище", user);
        return user;
    }




    // вспомогательный метод валидации экземпляра пользователя
    private boolean isNotValid(User user) {
        // логин не может быть пустым - проверено через аннотации и содержать пробелы
        return user.getLogin().contains(" ");
    }


}
