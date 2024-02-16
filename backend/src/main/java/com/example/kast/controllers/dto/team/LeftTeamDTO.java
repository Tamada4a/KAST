package com.example.kast.controllers.dto.team;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс используется при выходе или исключении игрока из команды
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeftTeamDTO {
    /**
     * Ник игрока
     */
    private String nick;

    /**
     * Название команды, из которой игрок вышел или был исключен
     */
    private String team;

    /**
     * Был ли игрок исключен из команды
     */
    private Boolean isKick;
}
