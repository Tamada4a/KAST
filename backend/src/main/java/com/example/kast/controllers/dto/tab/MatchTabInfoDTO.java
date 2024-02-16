package com.example.kast.controllers.dto.tab;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * Класс содержит информацию о матчах соответствующего турнира, в которых участвует игрок или команда
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@NoArgsConstructor
public class MatchTabInfoDTO {
    /**
     * Название турнира
     */
    private String event;

    /**
     * Занятое командой или игроком место на турнире.
     * Может быть пустым, если команда на текущий момент команда не заняла никакого места на турнире
     */
    private String place;

    /**
     * Тип матча:
     * <li><b>upcoming</b> - ближайшие матчи</li>
     * <li><b>ended</b> - завершенные матчи</li>
     */
    private String type;

    /**
     * Список объектов класса {@link EventMatchDTO}, содержащих информацию о матчах
     */
    private ArrayList<EventMatchDTO> matches;
}
