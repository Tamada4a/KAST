package com.example.kast.controllers.dto.player;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит информацию о созданной команде
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewTeamDTO {
    /**
     * Название команды
     */
    private String name;

    /**
     * Тэг команды
     */
    private String tag;

    /**
     * Страна команды
     */
    private String country;

    /**
     * Город команды. Может быть пустым
     */
    private String city;

    /**
     * Капитан команды - игрок, создавший команду
     */
    private String cap;
}
