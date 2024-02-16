package com.example.kast.controllers.dto.match;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс ставит в соответствие стороне (CT или T) каждой команды счет, с которым команда закончила соответствующую сторону.
 * <br></br>
 * Например: <i>команда ПУПА за сторону CT выиграла 7 раундов, команда Dream Team за сторону T выиграла 5 раундов</i>
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoresDTO {
    /**
     * Счет первой команды
     */
    private Integer scoreFirst;

    /**
     * Сторона первой команды: CT или T
     */
    private String sideFirst;

    /**
     * Счет второй команды
     */
    private Integer scoreSecond;

    /**
     * Сторона второй команды: CT или T
     */
    private String sideSecond;
}
