package com.example.kast.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * Данный класс содержит информацию об ошибке
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@Data
@Builder
public class ErrorDTO {
    /**
     * Сообщение об ошибке
     */
    private String message;
}
