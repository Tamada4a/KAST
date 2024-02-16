package com.example.kast.controllers.dto.match;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс описывает основную информацию о стриме. Используется для хранения в базе данных
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamDTO {
    /**
     * Страна стримера или язык трансляции
     */
    private String country;

    /**
     * Ник стримера
     */
    private String name;

    /**
     * Ссылка на трансляцию
     */
    private String link;
}
