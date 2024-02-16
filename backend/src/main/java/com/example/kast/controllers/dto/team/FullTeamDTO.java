package com.example.kast.controllers.dto.team;


import com.example.kast.controllers.dto.other.FlagNameSrcDTO;
import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.controllers.dto.tab.EventInfoDTO;
import com.example.kast.controllers.dto.tab.EventParticipantsDTO;
import com.example.kast.controllers.dto.tab.MatchTabInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс содержит информацию, необходимую для взаимодействия со страницей команды на frontend
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class FullTeamDTO {
    /**
     * Является ли пользователь участником команды
     */
    private boolean isParticipant;

    /**
     * Является ли пользователь капитаном данной команды
     */
    private boolean isCaptain;

    /**
     * Объект класса {@link TeamInfoDTO}, содержащий основную информацию о команде
     */
    private TeamInfoDTO teamInfo;

    /**
     * Список объектов класса {@link MatchTabInfoDTO}, содержащий информацию о ближайших матчах команды,
     * распределенных по турнирам, в рамках которого они проходят
     */
    private ArrayList<MatchTabInfoDTO> upcomingMatches;

    /**
     * Список объектов класса {@link MatchTabInfoDTO}, содержащий информацию о прошедших матчах команды,
     * распределенных по турнирам, в рамках которого они проходят
     */
    private ArrayList<MatchTabInfoDTO> endedMatches;

    /**
     * Список объектов класса {@link EventParticipantsDTO}, содержащих информацию о ближайших и текущих турнирах,
     * в которых примет участие команда
     */
    private ArrayList<EventParticipantsDTO> upcomingEvents;

    /**
     * Список объектов класса {@link EventInfoDTO}, содержащих информацию о завершенных турнирах, в которых участвовала
     * команда
     */
    private ArrayList<EventInfoDTO> endedEvents;

    /**
     * Список объектов класса {@link EventInfoDTO}, содержащих информацию о призовых местах на Lan турнирах
     */
    private ArrayList<EventInfoDTO> lanAchievements;

    /**
     * Список объектов класса {@link EventInfoDTO}, содержащих информацию о призовых местах на Online турнирах
     */
    private ArrayList<EventInfoDTO> onlineAchievements;

    /**
     * Список объектов класса {@link FlagNameSrcDTO}, содержащих информацию о бывших участниках команды
     */
    private ArrayList<FlagNameSrcDTO> exPlayers;

    /**
     * Список объектов класса {@link NameDTO}, содержащих названия турниров, на которых команда занимала призовые места
     */
    private ArrayList<NameDTO> trophies;
}
