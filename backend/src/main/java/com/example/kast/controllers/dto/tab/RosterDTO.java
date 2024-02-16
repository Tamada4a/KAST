package com.example.kast.controllers.dto.tab;

import com.example.kast.controllers.dto.other.NameDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс содержит информацию о команде, в которой состоял или состоит игрок
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class RosterDTO {
    /**
     * Название команды
     */
    private String team;

    /**
     * Период, в течение которого игрок находился или находится в составе.<br></br>
     * Если игрок покинул данную команду, то переменная имеет вид: <i>Месяц год</i> - <i>Месяц год</i>.<br></br>
     * Если игрок находится в команде, то переменная имеет вид: <i>Месяц год</i> - <i>Настоящее время</i>
     */
    private String period;

    /**
     * Список объектов класса {@link NameDTO}, содержащих название турниров,
     * на которых игрок занимал первое место с данной командой
     */
    private ArrayList<NameDTO> trophies;

    /**
     * Сколько дней игрок находится в команде
     */
    private long dayDiff;
}
