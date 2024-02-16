package com.example.kast.controllers.dto.match.twitch;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс содержит поле курсор для пагинации по списку полученных стримов
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class TwitchViewersPaginationDTO {
    /**
     * Используется для перехода по страницам списка полученных стримов
     */
    private String cursor;
}
