package com.example.kast.controllers.dto.tab;


import com.example.kast.mongo_collections.embedded.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс содержит информацию о ближайших и текущих турнирах со списком участников.
 * Используется во вкладке "Ближайшие и текущие турниры"
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class EventParticipantsDTO {
    /**
     * Название турнира
     */
    private String name;

    /**
     * Дата турнира в формате дд.мм.гггг - дд.мм.гггг
     */
    private String date;

    /**
     * Список объектов класса {@link Requests}, содержащий информацию об участниках турнира
     */
    ArrayList<Requests> participants;
}
