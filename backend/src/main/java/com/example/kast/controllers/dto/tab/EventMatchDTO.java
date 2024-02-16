package com.example.kast.controllers.dto.tab;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс описывает структуру матча, отображаемого во вкладке матчей на странице игрока или команды
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMatchDTO {
    /**
     * ID матча
     */
    private int matchId;

    /**
     * Дата начала матча в формате дд.мм.гггг
     */
    private String date;

    /**
     * Название левой команды
     */
    private String leftTeam;

    /**
     * Тэг левой команды
     */
    private String leftTag;

    /**
     * Название правой команды
     */
    private String rightTeam;

    /**
     * Тэг правой команды
     */
    private String rightTag;

    /**
     * Счет левой команды. Может быть "-", если матч начался, но счет не был открыт. В ином случае является числом
     */
    private String leftScore;

    /**
     * Счет правой команды. Может быть "-", если матч начался, но счет не был открыт. В ином случае является числом
     */
    private String rightScore;
}
