package com.example.kast.controllers.dto.tournaments;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит информацию о прошедшем турнире, отображаемую на странице турниров
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndedEventDTO {
    /**
     * Название турнира
     * */
    private String event;

    /**
     * Путь до флага страны проведения турнира на frontend
     * */
    private String flagPath;

    /**
     * Страна проведения турнира
     * */
    private String country;

    /**
     * Город проведения турнира
     * */
    private String city;

    /**
     * Дата проведения турнира в формате дд.мм.гггг - дд.мм.гггг
     * */
    private String date;

    /**
     * Формат проведения турнира:
     * <li>1x1</li>
     * <li>2x2</li>
     * <li>5x5</li>
     * */
    private String format;

    /**
     * Тип проведения турнира:
     * <li>Lan</li>
     * <li>Online</li>
     * */
    private String type;

    /**
     * Число зарегистрированных команд
     * */
    private Integer registred;

    /**
     * Максимальное число команд-участниц
     * */
    private Integer total;

    /**
     * Взнос с команды для участия в турнире
     * */
    private String fee;

    /**
     * Призовой фонд турнира
     * */
    private String prize;
}
