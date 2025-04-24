package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {
    Long id;

    // логин не может быть пустым и null
    @NotBlank
    String login;

    // электронная почта не может быть пустой и должна содержать символ `@`
    @NotBlank
    @Email
    String email;

    // имя для отображения может быть пустым — в таком случае будет использован логин
    String name;

    // дата рождения не может быть в будущем и пустым.
    @Past
    @NotNull
    LocalDate birthday;

    // свойство friends в классе пользователя,
    // будет содержать список уникальных id пользователей добавляемых в друзья
    Set<Long> friends;

    // статус для связи «дружба» между двумя пользователями
    // неподтверждённая и подтверждённая (unconfirmed и confirmed) - два ключа
    Map<String,Set<Long>> friendsStatus;

}
