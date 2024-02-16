package com.example.kast.controllers.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Данный класс описывает форму регистрации
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationDTO {
    /**
     * Имя пользователя
     */
    private String firstName;

    /**
     * Фамилия пользователя
     */
    private String lastName;

    /**
     * Ник пользователя
     */
    private String nick;

    /**
     * Email пользователя
     */
    private String email;

    /**
     * Страна пользователя
     */
    private String country;

    /**
     * Пароль пользователя
     */
    private char[] password;
}
