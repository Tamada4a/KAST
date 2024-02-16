package com.example.kast.controllers.dto.match.map;


import com.example.kast.controllers.dto.match.MatchPlayerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс содержит статистику команды на карте
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class MapTeamDTO {
    /**
     * Название команды
     */
    private String name;

    /**
     * Сторона команды: CT или T
     */
    private String side;

    /**
     * Счет команды
     */
    private Integer score;

    /**
     * Список статистик игроков команды
     */
    private ArrayList<MatchPlayerDTO> players;
}
