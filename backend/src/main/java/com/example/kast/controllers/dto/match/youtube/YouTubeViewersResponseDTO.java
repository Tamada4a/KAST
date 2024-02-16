package com.example.kast.controllers.dto.match.youtube;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс описывает ответ на запрос к YouTube API для получения списка стримов.<br></br>
 * Подробнее в <a href="https://developers.google.com/youtube/v3/docs/videos?hl=ru#liveStreamingDetails.concurrentViewers">документации</a>
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class YouTubeViewersResponseDTO {
    /**
     * Список объектов класса {@link YouTubeViewersItemDTO} - данных о стримах
     */
    private ArrayList<YouTubeViewersItemDTO> items;
}
