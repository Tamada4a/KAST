package com.example.kast.controllers.dto.player;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит информацию об измененных данных социальной сети пользователя
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeSocialDTO {
    /**
     * Ник пользователя
     */
    private String player;

    /**
     * Новая ссылка на социальную сеть
     */
    private String link;

    /**
     * Какая социальная сеть была изменена:
     * <li>VK</li>
     * <li>Discord</li>
     * <li>Faceit</li>
     * <li>Steam</li>
     */
    private String social;
}
