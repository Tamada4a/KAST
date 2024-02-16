package com.example.kast.controllers.dto.match;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс содержит информацию об игроке и его статистику по прошествии карт(ы)
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class FullMatchPlayerStatsDTO {
    /**
     * Страна игрока
     */
    private String country;

    /**
     * Путь до флага страны игрока на frontend
     */
    private String flagPath;

    /**
     * Имя игрока
     */
    private String firstName;

    /**
     * Фамилия игрока
     */
    private String lastName;

    /**
     * Ник игрока
     */
    private String nick;

    /**
     * Количество убийств
     */
    private int kills;

    /**
     * Количество смертей
     */
    private int deaths;

    /**
     * Количество ассистов (помощей в убийтсве)
     */
    private int assists;

    /**
     * Средний урон
     */
    private double avg;
}
