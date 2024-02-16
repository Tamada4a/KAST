package com.example.kast.controllers.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Данный класс описывает форму логина
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CredentialsDTO {
    /**
     * Ник пользователя
     */
    private String nick;

    /**
     * Пароль пользователя
     */
    private char[] password;

    /**
     * Значение "запомнить меня"
     */
    private Boolean isRememberMe;
}
