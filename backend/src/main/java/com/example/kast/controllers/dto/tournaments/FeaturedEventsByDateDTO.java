package com.example.kast.controllers.dto.tournaments;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


/**
 * Класс содержит информацию о будущих турнирах, соответствующих определенной дате формата <i>Месяц год</i>
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class FeaturedEventsByDateDTO {
    /**
     * Дата начала турнира в формате <i>Месяц год</i>
     */
    private String date;

    /**
     * Список объектов класса {@link FeaturedEventDTO}, содержащих информацию о будущих турнирах
     */
    private List<FeaturedEventDTO> events;
}
