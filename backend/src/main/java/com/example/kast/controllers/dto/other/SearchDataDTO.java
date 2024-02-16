package com.example.kast.controllers.dto.other;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс описывает данные, среди которых осуществляется поиск в поисковой строке,
 * расположенной в заголовке страницы
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@Data
public class SearchDataDTO {
    /**
     * Список объектов класса {@link NameDTO} - названий всех турниров
     */
    private ArrayList<NameDTO> events;

    /**
     * Список объектов класса {@link NameDTO} - названий всех команд
     */
    private ArrayList<NameDTO> teams;

    /**
     * Список объектов класса {@link NameDTO} - ников всех игроков
     */
    private ArrayList<NameDTO> players;
}
