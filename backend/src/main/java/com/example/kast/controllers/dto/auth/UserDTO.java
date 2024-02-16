package com.example.kast.controllers.dto.auth;


import com.example.kast.mongo_collections.documents.PlayerDoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Данный класс является упрощенной формой {@link PlayerDoc} с токеном.
 * Используется чтоб не хранить токен в документе игрока
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    /**
     * Имя пользователя
     */
    private String firstName;

    /**
     * Фамилия пользователя
     */
    private String lastName;

    /**
     * Email пользователя
     */
    private String email;

    /**
     * Страна пользователя
     */
    private String country;

    /**
     * Ник пользователя
     */
    private String nick;

    /**
     * JWT токен пользователя
     */
    private String token;
}
