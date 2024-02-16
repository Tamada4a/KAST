package com.example.kast.controllers.dto.match.twitch;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс описывает ответ на запрос к Twitch API для получения списка стримов.<br></br>
 * Подробнее в <a href="https://dev.twitch.tv/docs/api/reference/#get-streams">документации</a>
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class TwitchViewersResponseDTO {
    /**
     * Список объектов класса {@link TwitchViewersDataDTO} - стримов
     */
    private ArrayList<TwitchViewersDataDTO> data;

    /**
     * Объект класса {@link TwitchViewersPaginationDTO} - информация,
     * используемая для переключения между страницами списка полученных стримов.
     * Объект будет пуст, если достигнута первая или последняя страница
     */
    private TwitchViewersPaginationDTO pagination;
}
