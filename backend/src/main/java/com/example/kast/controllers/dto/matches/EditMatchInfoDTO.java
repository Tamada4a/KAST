package com.example.kast.controllers.dto.matches;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;


/**
 * Класс описывает форму изменения информации матча, вызываемой в заголовке страницы матча
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class EditMatchInfoDTO {
    /**
     * Дата начала матча в формате дд.мм.гггг
     */
    private String date;

    /**
     * Время начала матча в формате чч:мм
     */
    private String time;

    /**
     * Название турнира, в рамках которого проходит матч
     */
    private String event;

    /**
     * Название левой команды
     */
    private String nameFirst;

    /**
     * Название правой команды
     */
    private String nameSecond;
}
