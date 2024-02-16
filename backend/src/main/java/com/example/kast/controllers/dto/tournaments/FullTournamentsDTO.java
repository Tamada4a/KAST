package com.example.kast.controllers.dto.tournaments;


import com.example.kast.mongo_collections.documents.CountryDoc;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс содержит информацию, необходимую для взаимодействия со страницей "Турниры" на frontend
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class FullTournamentsDTO {
    /**
     * Список объектов класса {@link EndedEventsByDateDTO}, содержащих информацию о прошедших турнирах,
     * отсортированных по дате начала
     */
    private ArrayList<EndedEventsByDateDTO> endedEvents;

    /**
     * Список объектов класса {@link FeaturedEventsByDateDTO}, содержащих информацию о будущих турнирах,
     * отсортированных по дате начала
     */
    private ArrayList<FeaturedEventsByDateDTO> featuredEvents;

    /**
     * Список объектов класса {@link OngoingEventDTO}, содержащих информацию о текущих турнирах
     */
    private ArrayList<OngoingEventDTO> ongoingEvents;

    /**
     * Является ли пользователь админом
     */
    private Boolean isAdmin;

    /**
     * Список объектов класса {@link CountryDoc}, содержащих информацию о странах.
     * Используется при создании новых турниров
     */
    private List<CountryDoc> countries;

    /**
     * Список всех карт, доступных в базе данных
     */
    private ArrayList<String> mapPool;
}
