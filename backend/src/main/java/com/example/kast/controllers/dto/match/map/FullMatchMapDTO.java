package com.example.kast.controllers.dto.match.map;


import com.example.kast.controllers.dto.match.FullMatchStatsDTO;
import com.example.kast.controllers.dto.match.ScoresDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит всю информацию о карте матча. Используется для передачи информации на frontend.
 * Является упрощенной формой {@link MapDTO}.
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FullMatchMapDTO {
    /**
     * Название карты
     */
    private String mapName;

    /**
     * Статус карты: upcoming (будет сыграна), ongoing (в процессе игры), ended (завершена)
     */
    private String status;

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
     * Объект класса {@link FullMatchStatsDTO}, содержащий статистику игроков каждой команды
     */
    private FullMatchStatsDTO stats;
}
