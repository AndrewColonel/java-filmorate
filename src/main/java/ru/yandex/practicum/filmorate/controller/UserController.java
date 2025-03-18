package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    // получение списка всех пользователей
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    // получение всех фильмов
    public User create(@RequestBody User user) {
        return user;
    }

    @PutMapping
    // обновление пользователя
    public User update(@RequestBody User newUser) {
        User oldUser = users.get(newUser.getId());
        return oldUser;
    }

    // вспомогательный метод получения следующего значения id
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
