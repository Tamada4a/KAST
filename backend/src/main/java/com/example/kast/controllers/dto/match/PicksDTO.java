package com.example.kast.controllers.dto.match;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит информацию о действии команды во время пика/бана карт.<br/>
 * Например: <i>команда ПУПА банит карту Nuke</i>
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PicksDTO {
    /**
     * Название команды. Может быть пустым в случае, когда выбирается десайдер
     */
    private String team;

    /**
     * Действие, совершенное с картой:
     * <li><b>pick</b> - команда выбрала карту</li>
     * <li><b>ban</b> - команда забаникла карту</li>
     * <li><b>decider</b> - оставшаяся последняя карта. В случае 2x2 отсутствует</li>
     */
    private String type;

    /**
     * Название карты, с которой было совершено действие
     */
    private String map;
}
