package com.example.kast.controllers.dto.tab;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит информацию о турнире, где игрок или команда занимали призовые места
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventInfoDTO {
    /**
     * Название турнира
     */
    private String name;

    /**
     * Занятое место
     */
    private String place;

    /**
     * Дата турнира в формате дд.мм.гггг - дд.мм.гггг
     */
    private String date;
}
