package com.example.kast.controllers.dto.player;


import com.example.kast.controllers.dto.other.FlagNameSrcDTO;
import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.controllers.dto.tab.EventInfoDTO;
import com.example.kast.controllers.dto.tab.EventParticipantsDTO;
import com.example.kast.controllers.dto.tab.MatchTabInfoDTO;
import com.example.kast.controllers.dto.tab.RosterDTO;
import com.example.kast.mongo_collections.embedded.PlayerFullStats;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс содержит информацию, необходимую для взаимодействия со страницей игрока на frontend
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class FullPlayerDTO {
    /**
     * Список объектов класса {@link SocialDTO}, содержащих информацию о социальных сетях игрока
     */
    private ArrayList<SocialDTO> social;

    /**
     * Является ли игрок администратором
     */
    private Boolean isAdmin;

    /**
     * Объект класса {@link NameDTO}, содержащий название команды игрока
     */
    private NameDTO teamName;

    /**
     * Список объектов класса {@link MatchTabInfoDTO}, содержащий информацию о грядущих матчах,
     * отсортированных по принадлежности к турниру
     */
    private ArrayList<MatchTabInfoDTO> matchesUpcoming;

    /**
     * Список объектов класса {@link MatchTabInfoDTO}, содержащий информацию о завершенных матчах,
     * отсортированных по принадлежности к турниру
     */
    private ArrayList<MatchTabInfoDTO> matchesEnded;

    /**
     * Список объектов класса {@link EventInfoDTO}, содержащих информацию о призовых местах на Lan турнирах
     */
    private ArrayList<EventInfoDTO> lanAchievements;

    /**
     * Список объектов класса {@link EventInfoDTO}, содержащих информацию о призовых местах на Online турнирах
     */
    private ArrayList<EventInfoDTO> onlineAchievements;

    /**
     * Объект класса {@link PlayerFullStats}, содержащий полную статистику игрока
     */
    private PlayerFullStats stats;

    /**
     * Список объектов {@link RosterDTO}, содержащих информацию о всех составах игрока
     */
    private ArrayList<RosterDTO> playerRosters;

    /**
     * Список объектов класса {@link EventParticipantsDTO}, содержащих информацию о грядущих турнирах игрока
     */
    private ArrayList<EventParticipantsDTO> upcomingEvents;

    /**
     * Список объектов класса {@link EventInfoDTO}, содержащих информацию о прошедших турнирах игрока
     */
    private ArrayList<EventInfoDTO> endedEvents;

    /**
     * Объект класса {@link FlagNameSrcDTO}, содержащий информацию об имени и фамилии игрока и его стране
     */
    private FlagNameSrcDTO flagName;

    /**
     * Возраст игрока
     */
    private String age;

    /**
     * Список объектов класса {@link NameDTO}, содержащий названия турниров, на которых игрок выигрывал титулы
     */
    private ArrayList<NameDTO> trophies;
}
