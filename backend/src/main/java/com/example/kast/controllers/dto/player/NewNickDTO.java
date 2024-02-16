package com.example.kast.controllers.dto.player;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит измененный ник соответствующего игрока
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewNickDTO {
    /**
     * Старый ник игрока
     */
    private String oldNick;

    /**
     * Новый ник игрока
     */
    private String newNick;
}
