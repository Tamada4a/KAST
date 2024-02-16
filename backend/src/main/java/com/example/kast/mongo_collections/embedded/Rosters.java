package com.example.kast.mongo_collections.embedded;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static com.example.kast.utils.Utils.fixNumber;


/**
 * Класс содержит информацию о команде игрока (текущей или бывшей)
 *
 * @author Кирилл "Tamada" Симовин, Александр "ugly4" Федякин
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Rosters {
    /**
     * Дата вступления в команду
     */
    private LocalDate enterDate;

    /**
     * Дата выхода из команды. Принимает значение null, если игрок на данный момент состоит в команде
     */
    private LocalDate exitDate;

    /**
     * Название команды
     */
    private String teamName;


    /**
     * Переопределенный метод строкового представления объекта класса {@link Rosters}
     *
     * @return Строковое представление объекта класса {@link Rosters}
     */
    @Override
    public String toString() {
        String enterDay = "";
        String enterMonth = "";
        String enterYear = "";
        if (enterDate != null) {
            enterDay = fixNumber(enterDate.getDayOfMonth());
            enterMonth = fixNumber(enterDate.getMonthValue());
            enterYear = String.valueOf(enterDate.getYear());
        }

        String exitDay = "";
        String exitMonth = "";
        String exitYear = "";
        if (exitDate != null) {
            exitDay = fixNumber(exitDate.getDayOfMonth());
            exitMonth = fixNumber(exitDate.getMonthValue());
            exitYear = String.valueOf(exitDate.getYear());
        }

        return String.format("Rosters(enterDate=%s-%s-%s, exitDate=%s-%s-%s, teamName=%s)",
                enterDay, enterMonth, enterYear, exitDay, exitMonth, exitYear, teamName);
    }
}
