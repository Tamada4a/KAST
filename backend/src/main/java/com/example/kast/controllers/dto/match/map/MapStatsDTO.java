package com.example.kast.controllers.dto.match.map;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит статистику команд по сторонам (CT или T) на карте
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapStatsDTO {
    /**
     * Объект класса {@link MapTeamDTO}, содержащий информацию о первой команде:
     * <li>Название</li>
     * <li>Сторону: CT или T</li>
     * <li>Счет</li>
     * <li>Статистику игроков</li>
     */
    private MapTeamDTO firstTeam;

    /**
     * Объект класса {@link MapTeamDTO}, содержащий информацию о второй команде:
     * <li>Название</li>
     * <li>Сторону: CT или T</li>
     * <li>Счет</li>
     * <li>Статистику игроков</li>
     */
    private MapTeamDTO secondTeam;
}
