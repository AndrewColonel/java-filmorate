package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    // получение списка всех пользователей
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    // получение всех фильмов
    public User create(@RequestBody User user) {
        if (isNotValid(user)) {
            throw new ValidationException("Неверные данные о пользователе");
        }
        user.setId(getNextId());
        if ( user.getName() == null || user.getName().isBlank() ) {
            user.setName(user.getLogin());
        } else {
            user.setName(user.getName());
        }
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    // обновление пользователя
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            if (isNotValid(newUser)) {
                throw new ValidationException("Неверные данные о пользователе");
            }
            User oldUser = users.get(newUser.getId());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            if (newUser.getName() == null || newUser.getName().isBlank() ) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // вспомогательный метод валидации экземпляра пользователя
    private boolean isNotValid(User user) {
        return user.getEmail().isBlank()
                || !user.getEmail().contains("@")
                || user.getLogin() == null
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now());
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
