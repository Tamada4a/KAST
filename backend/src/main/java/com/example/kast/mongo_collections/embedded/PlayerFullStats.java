package com.example.kast.mongo_collections.embedded;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит полную статистику игрока по всем сыгранным матчам
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerFullStats {
    /**
     * Количество убийств
     */
    private int kills;

    /**
     * Количество убийств в голову
     */
    private int hsKills;

    /**
     * Количество сыгранных карт
     */
    private int maps;

    /**
     * Количество сыгранных раундов
     */
    private int roundsPlayed;

    /**
     * Общее количество нанесенного урона
     */
    private double fullDamage;

    /**
     * Количество смертей
     */
    private int deaths;
}
