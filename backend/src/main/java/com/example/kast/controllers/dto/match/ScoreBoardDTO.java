package com.example.kast.controllers.dto.match;


import com.example.kast.controllers.dto.match.map.MapStatsDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


/**
 * Класс описывает таблицу на странице матча. Является расширением {@link MapStatsDTO}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
public class ScoreBoardDTO extends MapStatsDTO {
    /**
     * Текущий раунд на карте
     */
    private Integer currentRound;

    /**
     * Список объектов класса {@link RoundHistoryDTO} - иконок,
     * описывающих каким образом завершились предыдущие раунды на карте
     */
    private ArrayList<RoundHistoryDTO> roundHistory;

    /**
     * Статус карты:
     * <li><b>upcoming</b> - будет сыграна</li>
     * <li><b>ongoing</b> - в процессе игры</li>
     * <li><b>ended</b> - завершена</li>
     */
    private String status;

    /**
     * Название карты, которая играется в данный момент
     */
    private String mapName;


    /**
     * Инициализирует и создает новый объект класса ScoreBoardDTO
     *
     * @param mapStats     объект класса {@link MapStatsDTO}, содержащий статистику игроков каждой команды на карте
     * @param currentRound текущий раунд на карте
     * @param roundHistory список объектов класса {@link RoundHistoryDTO}, содержащий информацию о прошедших раундах
     * @param status       статус карты:
     *                     <li><b>upcoming</b> - будет сыграна</li>
     *                     <li><b>ongoing</b> - в процессе игры</li>
     *                     <li><b>ended</b> - завершена</li>
     * @param mapName      название текущей карты
     */
    public ScoreBoardDTO(MapStatsDTO mapStats, int currentRound, ArrayList<RoundHistoryDTO> roundHistory, String status, String mapName) {
        this.setFirstTeam(mapStats.getFirstTeam());
        this.setSecondTeam(mapStats.getSecondTeam());
        this.currentRound = currentRound;
        this.roundHistory = roundHistory;
        this.status = status;
        this.mapName = mapName;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link ScoreBoardDTO}
     *
     * @return Строковое представление объекта класса {@link ScoreBoardDTO}
     */
    @Override
    public String toString() {
        return String.format("ScoreBoardDTO(firstTeam=%s, secondTeam=%s, currentRound=%d, roundHistory=%s, status=%s, " +
                        "mapName=%s)", this.getFirstTeam().toString(), this.getSecondTeam().toString(), currentRound,
                roundHistory.toString(), status, mapName);
    }
}
