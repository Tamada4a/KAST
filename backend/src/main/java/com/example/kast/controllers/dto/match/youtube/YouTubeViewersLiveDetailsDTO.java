package com.example.kast.controllers.dto.match.youtube;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс описывает количество зрителей стрима
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class YouTubeViewersLiveDetailsDTO {
    /**
     * Количество зрителей стрима
     */
    private String concurrentViewers;
}
