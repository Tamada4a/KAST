package com.example.kast.controllers.dto.match.youtube;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс содержит поле liveStreamingDetails с метаданными о стриме
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class YouTubeViewersItemDTO {
    /**
     * Объект класса {@link YouTubeViewersLiveDetailsDTO}, содержащий метаданные о стриме.
     * Объект не <i>null</i>, если видео является предстоящей, текущей или прошедшей трансляцией
     */
    private YouTubeViewersLiveDetailsDTO liveStreamingDetails;
}
