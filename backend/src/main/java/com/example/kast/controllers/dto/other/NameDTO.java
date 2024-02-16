package com.example.kast.controllers.dto.other;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит информацию о названии турнира/команды или нике игрока
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NameDTO {
    /**
     * Название команды/турнира или ник игрока
     */
    private String name;
}
