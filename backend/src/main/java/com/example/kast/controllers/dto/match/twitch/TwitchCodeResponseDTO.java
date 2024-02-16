package com.example.kast.controllers.dto.match.twitch;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс описывает ответ на запрос к Twitch API для получения токена доступа пользователя (user access token). <br></br>
 * Подробнее в <a href="https://dev.twitch.tv/docs/authentication/getting-tokens-oauth/#client-credentials-grant-flow">документации</a>
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class TwitchCodeResponseDTO {
    /**
     * Аутентифицированный токен, который будет использоваться для получения списка стримов
     */
    private String access_token;

    /**
     * Время, которое код будет действительным
     */
    private Integer expires_in;

    /**
     * Тип токена. Ожидается, что он будет Bearer
     */
    private String token_type;
}
