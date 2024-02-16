package com.example.kast.controllers.dto.matches;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс ставит в соответствие название команды и ее тэг
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@Data
public class TeamNameTagDTO {
    /**
     * Название команды
     */
    private String name;

    /**
     * Тэг команды
     */
    private String tag;
}
