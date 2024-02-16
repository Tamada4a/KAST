package com.example.kast.controllers.dto.match;


import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.mongo_collections.documents.CountryDoc;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс содержит необходимую информацию для взаимодействия со страницей матча на frontend
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@Data
public class FullMatchDTO {
    /**
     * Объект класса {@link ChosenMatchDTO}, содержащий всю информацию о самом матче
     */
    private ChosenMatchDTO match;

    /**
     * Список объектов класса {@link CountryDoc} - всех стран, доступных в базе данных.
     * Используется для выбора страны при добавлении трансляции
     */
    private List<CountryDoc> countries;

    /**
     * Список объектов класса {@link FullMatchTeamDTO} - всех команд, участвующих в турнире.
     * Используется для изменения команд, участвующих в матче
     */
    private ArrayList<FullMatchTeamDTO> teams;

    /**
     * Список объектов {@link NameDTO} - всех доступных турниров.
     * Используется для изменения турнира, в рамках которого проходит матч
     */
    private ArrayList<NameDTO> allEvents;

    /**
     * Является ли пользователь участником матча
     */
    private Boolean isParticipant;

    /**
     * Является ли пользователь администратором
     */
    private Boolean isAdmin;
}
