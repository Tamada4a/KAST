package com.example.kast.controllers.dto.match;


import com.example.kast.controllers.dto.match.map.FullMatchMapDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;


/**
 * Класс содержит всю информацию о матче
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChosenMatchDTO {
    /**
     * Статус матча:
     * <li><b>0</b> - матч пока не начался</li>
     * <li><b>1</b> - матч играется</li>
     * <li><b>2</b> - матч закончен</li>
     */
    private Integer matchStatus;

    /**
     * Дата и время начала матча
     */
    private LocalDateTime matchDate;

    /**
     * IP-адрес сервера, на котором играется матч. Виден только игрокам, участвующим в матче
     */
    private String ip;

    /**
     * Список объектов {@link FullMatchDTO} - карт матча
     */
    private ArrayList<FullMatchMapDTO> maps;

    /**
     * Название турнира, в рамках которого проходит матч
     */
    private String event;

    /**
     * Формат матча - сколько карт играется. Как правило матчи проходят в одном из следующих форматов:
     * <li><b>best of 1 (bo1)</b> - играется одна карта</li>
     * <li><b>best of 2 (bo2)</b> - играется две карты, может быть ничья</li>
     * <li><b>best of 3 (bo3)</b> - играется максимум три карты, игра до двух побед</li>
     * <li><b>best of 5 (bo5)</b> - играется максимум пять карт, игра до трех побед</li>
     * <li><b>best of 7 (bo7)</b> - играется максимум семь карт, игра до четырех побед</li>
     */
    private String format;

    /**
     * Тип матча (соответствует типу турнира): Lan или Online
     */
    private String type;

    /**
     * Тип команды-участницы: team (команда) или player (игрок)
     */
    private String partType;

    /**
     * Описание матча
     */
    private String description;

    /**
     * Список карт турнира, из которого пикаются и банятся карты
     */
    private ArrayList<String> mapPool;

    /**
     * Список объектов класса {@link PicksDTO} - действий команды при пике/бане карт. Например:
     * <i>команда ПУПА банит карту Nuke</i>
     */
    private ArrayList<PicksDTO> picks;

    /**
     * Объект класса {@link FullMatchTeamDTO}, описывающий первую команду матча
     */
    private FullMatchTeamDTO firstTeam;

    /**
     * Объект класса {@link FullMatchTeamDTO}, описывающий вторую команду матча
     */
    private FullMatchTeamDTO secondTeam;
}
