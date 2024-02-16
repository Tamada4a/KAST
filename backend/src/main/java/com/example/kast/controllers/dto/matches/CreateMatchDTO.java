package com.example.kast.controllers.dto.matches;


import com.example.kast.controllers.dto.match.map.MapDTO;
import com.example.kast.mongo_collections.embedded.Matches;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


/**
 * Класс описывает форму создания нового матча. Является упрощенной формой {@link Matches}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMatchDTO {
    /**
     * Дата начала матча в формате дд.мм.гггг
     */
    private String date;

    /**
     * Название турнира, в рамках которого проходит матч
     */
    private String event;

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
     * Список объектов класса {@link MapDTO} - карт матча. В данном случае имеет вид:<br></br>
     * <code>[{mapName: "TBA", status: "upcoming"}, ...]</code>
     */
    private ArrayList<MapDTO> maps;

    /**
     * Важность матча (от 1 до 5 звезд)
     */
    private Integer tier;

    /**
     * Время начала матча в формате чч:мм
     */
    private String time;

    /**
     * ID матча
     */
    private Integer matchId;

    /**
     * Описание матча
     */
    private String description;
}
