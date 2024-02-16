package com.example.kast.controllers.dto.match;

import com.example.kast.controllers.dto.other.FlagNameSrcDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


/**
 * Класс содержит информацию о команде и ее игроках.
 * Используется для отображения составов команд на странице матча
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullMatchTeamDTO {
    /**
     * Название команды (в случае 2x2 или 5x5)/ник игрока (в случае 1x1)
     */
    private String name;

    /**
     * Путь до флага страны команды на frontend
     */
    private String flagPath;

    /**
     * Название команды. Используется в матчах 1x1 для отображения команды игрока
     */
    private String team;

    /**
     * Позиция команды в рейтинге. Если матч в формате 1x1, переменная принимает значение -1
     */
    private Integer topPos;

    /**
     * Счет команды в матче
     */
    private Integer score;

    /**
     * Список объектов {@link FlagNameSrcDTO} - информации об игроках-участниках:
     * <li>Ник</li>
     * <li>Страна</li>
     * <li>Путь до флага страны игрока на frontend</li>
     */
    private ArrayList<FlagNameSrcDTO> players;
}