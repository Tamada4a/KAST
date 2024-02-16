package com.example.kast.controllers.dto.matches;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


/**
 * Класс содержит информацию о будущих матчах, соответствующих определенной дате
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class MatchTimeByDateDTO {
    /**
     * Дата начала матчей в формате дд.мм.гггг
     */
    private String date;

    /**
     * Список объектов {@link MatchTimeDTO}, содержащих информацию о будущих матчах:
     * <li>Дата и время начала матча</li>
     * <li>Название и тэги команд</li>
     * <li>Счет на текущей карте и по картам</li>
     * <li>Значимость матча (от 1 до 5 звезд)</li>
     * <li>Название турнира, в рамках которого проходит турнир</li>
     * <li>Список карт, которые будут играться в матче</li>
     */
    private List<MatchTimeDTO> matches;
}
