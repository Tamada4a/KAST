package com.example.kast.controllers.dto.player;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит измененную дату рождения соответствующего игрока
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewDateDTO {
    /**
     * Новая дата рождения в формате дд.мм.гггг
     */
    private String bdate;

    /**
     * Ник игрока
     */
    private String player;
}
