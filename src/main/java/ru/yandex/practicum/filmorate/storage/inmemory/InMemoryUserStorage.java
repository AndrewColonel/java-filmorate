package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Slf4j
//@Primary
public class InMemoryUserStorage implements UserStorage {

    static final Map<Long, User> users = new HashMap<>();

    // получение списка всех пользователей
    @Override
    public Collection<User> findAll() {
        log.trace("Получение списка всех пользователей");
        return users.values();
    }

    // поиск пользователя по ID
    @Override
    public User findUserById(long id) {
        User user = users.get(id);
        if (user == null) throw new NotFoundException(String.format("Пользователя с ID %d не существует.", id));
        return user;
    }

    // создание нового пользователя
    @Override
    public User create(User user) {
        log.trace("Начата обработка данных для создания нового пользователя");
        if (isNotValid(user)) {
            log.debug("Пользователь {} не прошел валидацию при создании", user);
            throw new ValidationException("Неверные данные о пользователе");
        }
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else {
            user.setName(user.getName());
        }
        // при создании c lombok контрсутором список остался null- необходимо создать пустой список
        if (user.getFriends() == null) user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.debug("Пользователь {} добавлен в хранилище", user);
        return user;
    }

    // обновление имеющегося пользователя
    @Override
    public User update(User newUser) {
        log.trace("Начата обработка данных для обновления информации об имеющемся пользователе");
        if (newUser.getId() == null) {
            log.error("не указан ID при обновлении для пользователя {}", newUser);
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            if (isNotValid(newUser)) {
                log.debug("Пользователь {} не прошел валидацию при обновлении", newUser);
                throw new ValidationException("Неверные данные о пользователе");
            }
            User oldUser = users.get(newUser.getId());
            // если передали список друзей, надо его принять
            if (newUser.getFriends() != null) {
                oldUser.setFriends(newUser.getFriends());
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            // имя для отображения может быть пустым — в таком случае будет использован логин
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            log.debug("Пользователь {} обновлен в хранилище", oldUser);
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public void addFriend(long userId, long friendId) {
    }

    @Override
    public void delFriend(long userId, long friendId) {
    }


    // вспомогательный метод валидации экземпляра пользователя
    private boolean isNotValid(User user) {
        // логин не может быть пустым - проверено через аннотации и содержать пробелы
        return user.getLogin().contains(" ");

    }

    // вспомогательный метод получения следующего значения id
    private long getNextId() { //dfsgfsd
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
