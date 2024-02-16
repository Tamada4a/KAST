package com.example.kast.controllers.dto.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс, ставящий в соответствие игрока и его команду.
 * Массив объектов данного класса используется для выбора MVP турнира
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamNickDTO {
    /**
     * Ник игрока
     */
    private String nick;

    /**
     * Команда игрока
     */
    private String team;
}
