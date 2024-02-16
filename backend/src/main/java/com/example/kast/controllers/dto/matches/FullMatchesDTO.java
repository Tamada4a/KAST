package com.example.kast.controllers.dto.matches;


import com.example.kast.controllers.dto.other.NameDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс содержит информацию, необходимую для взаимодействия со страницей "Матчи" на frontend
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@Data
public class FullMatchesDTO {
    /**
     * Является ли пользователь админом
     */
    private Boolean isAdmin;

    /**
     * Список объектов класса {@link TeamNameTagDTO} - пар <i>название команды - тег команды</i>
     */
    private ArrayList<TeamNameTagDTO> teams;

    /**
     * Список объектов класса {@link NameDTO}, содержащих название всех турниров
     */
    private ArrayList<NameDTO> allEvents;

    /**
     * Список объектов класса {@link MatchDTO}, содержащих информацию о текущих матчах:
     * <li>Дата и время начала матча</li>
     * <li>Название и тэги команд</li>
     * <li>Счет на текущей карте и по картам</li>
     * <li>Значимость матча (от 1 до 5 звезд)</li>
     * <li>Название турнира, в рамках которого проходит турнир</li>
     * <li>Список карт, которые будут играться в матче</li>
     */
    private ArrayList<MatchDTO> ongoingMatches;

    /**
     * Список объектов класса {@link MatchTimeByDateDTO}, содержащих информацию о ближайших матчах,
     * отсортированных по дате:
     * <li>Дата и время начала матча</li>
     * <li>Название и тэги команд</li>
     * <li>Счет на текущей карте и по картам</li>
     * <li>Значимость матча (от 1 до 5 звезд)</li>
     * <li>Название турнира, в рамках которого проходит турнир</li>
     * <li>Список карт, которые будут играться в матче</li>
     */
    private ArrayList<MatchTimeByDateDTO> upcomingMatches;
}
