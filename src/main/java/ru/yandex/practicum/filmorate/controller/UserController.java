package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    // получение списка всех пользователей
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    // получение всех фильмов
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    // обновление пользователя
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    // добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriends(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        return userService.addFriends(userId, friendId);
    }

    // удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public User delFriends(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        return userService.delFriends(userId, friendId);
    }

    // возвращаем список пользователей, являющихся его друзьями
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable("id") long id) {
        return userService.getFriends(id);
    }

    // список друзей, общих с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") long userId,
                                             @PathVariable("otherId") long otherId) {
        return userService.getCommonFriends(userId, otherId);
    }
}
