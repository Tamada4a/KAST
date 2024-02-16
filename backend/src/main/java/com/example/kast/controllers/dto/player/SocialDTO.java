package com.example.kast.controllers.dto.player;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит информацию о социальной сети игрока
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialDTO {
    /**
     * Название социальной сети
     */
    private String alt;

    /**
     * Цвет иконки социальной сети: белый или цветной. От этого зависит выбор класса при стилизации на frontend
     */
    private String color;

    /**
     * Ссылка на социальную сеть
     */
    private String link;

    /**
     * Путь до иконки социальной сети на frontend
     */
    private String src;
}
