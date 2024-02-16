package com.example.kast.controllers.dto.tab;


import lombok.Data;


/**
 * Класс содержит измененное описание соответствующей команды
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
public class ChangeDescriptionDTO {
    /**
     * Название команды
     */
    private String team;

    /**
     * Текст описания
     */
    private String description;
}
