package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    Long id;

    // свойство likes в классе abkmvs,
    // будет содержать список уникальных id пользователей поставивших лайк
    Set<Long> likes;

    // название не может быть пустым и null
    @NotBlank
    String name;

    // описание может быть null, но если есть то не более 200 символов,
    // проверка валидности в контроллере
    String description;

    // дата выпуска должна быть не null,
    // проверка на валидность самой даты выполняется в контроллере
    @NotNull
    LocalDate releaseDate;

    // продолжительность фильма должна быть положительным числом и не null
    @Positive
    @NotNull
    Integer duration;

    // метод для добавления id пользователей во множество
    public void setLike(long id) {
        likes.add(id);
    }

    // метод для удаления id пользователей во множество
    public void delLike(long id) {
        likes.remove(id);
    }

}


