package com.example.kast.controllers.dto.other;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит информацию о нике игрока и его стране
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlagNameSrcDTO {
    /**
     * Страна игрока
     */
    private String country;

    /**
     * Ник или имя игрока
     */
    private String name;

    /**
     * Путь до флага страны игрока на frontend
     */
    private String flagPath;
}
