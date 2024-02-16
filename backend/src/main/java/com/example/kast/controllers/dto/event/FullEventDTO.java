package com.example.kast.controllers.dto.event;


import com.example.kast.controllers.dto.matches.MatchDTO;
import com.example.kast.controllers.dto.matches.MatchTimeByDateDTO;
import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.mongo_collections.documents.CountryDoc;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс содержит всю информацию для взаимодействия со страницей турнира
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@Data
public class FullEventDTO {
    /**
     * Объект класса {@link ChosenEventDTO}. Содержит основную информацию о выбранном турнире
     */
    private ChosenEventDTO tournament;

    /**
     * Список всех доступных карт, которые можно добавить в маппул турнира
     */
    private ArrayList<String> eventMapPool;

    /**
     * Список текущих матчей турнира
     */
    private ArrayList<MatchDTO> ongoingMatches;

    /**
     * Список будущих матчей турнира
     */
    private ArrayList<MatchTimeByDateDTO> upcomingMatches;

    /**
     * Список законченных матчей турнира
     */
    private ArrayList<MatchTimeByDateDTO> results;

    /**
     * Участвует ли команда пользователя в турнире
     */
    private Boolean activeTour;

    /**
     * Является ли пользователь капитаном какой-либо команды
     */
    private Boolean isCap;

    /**
     * Является ли пользователь админом
     */
    private Boolean isAdmin;

    /**
     * Список всех стран
     */
    private List<CountryDoc> countries;

    /**
     * Список тиммейтов пользователя
     */
    private ArrayList<NameDTO> players;

    /**
     * Объект класса {@link ParticipantDTO}. Описывает команду игрока на турнире:
     * <li><b>chosenPlayers</b> - заявленные игроки</li>
     * <li><b>status</b> - статус команды в зависимости от стадии турнира:
     * заявка на рассмотрении, заявка принята, команда участвует на турнире, команда исключена с турнира</li>
     * <li><b>tag</b> - тэг команды</li>
     * <li><b>team</b> - название команды (для турниров 1x1)</li>
     * <li><b>teamName</b> - название команды/имя участника</li>
     * <li><b>type</b> - тип заявки: team (команда) или player (игрок)</li>
     */
    private ParticipantDTO playerTeam;

    /**
     * Ник игрока, ставшего MVP турнира
     */
    private String mvp;
}
