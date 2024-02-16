package com.example.kast.controllers.dto.match;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


/**
 * Класс содержит информацию о командах и статистике игроков каждой команды
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FullMatchStatsDTO {
    /**
     * Название первой команды
     */
    private String firstTeam;

    /**
     * Список объектов класса {@link FullMatchPlayerStatsDTO} - информации о каждом игроке первой команды
     */
    private ArrayList<FullMatchPlayerStatsDTO> firstTeamPlayers;

    /**
     * Название второй команды
     */
    private String secondTeam;

    /**
     * Список объектов класса {@link FullMatchPlayerStatsDTO} - информации о каждом игроке второй команды
     */
    private ArrayList<FullMatchPlayerStatsDTO> secondTeamPlayers;
}
