package com.example.kast.controllers.dto.match;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс содержит информацию о счете матча на текущей карте и счете по картам.
 * Используется для отображения счета на странице матчей
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class MatchScoreDTO {
    /**
     * Счет левой команды на текущей карте
     */
    private Integer leftScore;

    /**
     * Счет правой команды на текущей карте
     */
    private Integer rightScore;

    /**
     * Счет левой команды по картам
     */
    private Integer leftMapScore;

    /**
     * Счет правой команды по картам
     */
    private Integer rightMapScore;
}
