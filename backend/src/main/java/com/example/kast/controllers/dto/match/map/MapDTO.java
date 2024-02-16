package com.example.kast.controllers.dto.match.map;


import com.example.kast.controllers.dto.match.ScoresDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


/**
 * Класс содержит полную информацию о карте матча. Используется для хранения в базе данных
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapDTO {
    /**
     * Название карты
     */
    private String mapName;

    /**
     * Статус карты:
     * <li><b>upcoming</b> - будет сыграна</li>
     * <li><b>ongoing</b> - в процессе игры</li>
     * <li><b>ended</b> - завершена</li>
     */
    private String status;

    /**
     * Список объектов {@link LogDTO} - логи карты
     */
    private ArrayList<LogDTO> logs;

    /**
     * Объект класса {@link MapStatsDTO}, хранящий статистику игроков каждой команды на карте
     */
    private MapStatsDTO stats;

    /**
     * Объект класса {@link ScoresDTO}, содержащий информацию о первой половине матча
     */
    private ScoresDTO firstHalf;

    /**
     * Объект класса {@link ScoresDTO}, содержащий информацию о второй половине матча
     */
    private ScoresDTO secondHalf;

    /**
     * Объект класса {@link ScoresDTO}, содержащий информацию об овертаймах
     */
    private ScoresDTO overtime;

    /**
     * Текущий раунд на карте
     */
    private Integer currentRound;
}
