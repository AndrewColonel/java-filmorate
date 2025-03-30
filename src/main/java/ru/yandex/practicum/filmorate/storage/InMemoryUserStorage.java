package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    // получение списка всех пользователей
    @Override
    public Collection<User> findAll() {
        log.trace("Получение списка всех пользователей");
        return users.values();
    }

    // получение всех фильмов
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
        users.put(user.getId(), user);
        log.debug("Пользователь {} добавлен в хранилище", user);
        return user;
    }

    // обновление пользователя
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

    // вспомогательный метод валидации экземпляра пользователя
    private boolean isNotValid(User user) {
        // логин не может быть пустым - проверено через аннотации и содержать пробелы
        return user.getLogin().contains(" ");

    }

    // вспомогательный метод получения следующего значения id
    private int getNextId() { //dfsgfsd
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
