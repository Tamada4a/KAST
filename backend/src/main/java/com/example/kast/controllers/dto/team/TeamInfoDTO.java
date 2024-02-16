package com.example.kast.controllers.dto.team;


import com.example.kast.controllers.dto.other.FlagNameSrcDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс содержит основную информацию о команде
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@Data
public class TeamInfoDTO {
    /**
     * Город команды
     */
    private String city;

    /**
     * Страна команды
     */
    private String country;

    /**
     * Текст описания команды
     */
    private String description;

    /**
     * Позиция команды в топе. Если команда не находится в топе, принимает значение -999
     */
    private int topPosition;

    /**
     * Название команды
     */
    private String name;

    /**
     * Путь до флага страны команды на frontend
     */
    private String flagPath;

    /**
     * Список объектов класса {@link FlagNameSrcDTO}, содержащих информацию об активных игроках команды
     */
    private ArrayList<FlagNameSrcDTO> players;
}
