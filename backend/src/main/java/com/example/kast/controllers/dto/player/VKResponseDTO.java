package com.example.kast.controllers.dto.player;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс описывает ответ на запрос к VK API для получения токена доступа (access token). <br></br>
 * Подробнее в
 * <a href="https://dev.vk.com/ru/api/access-token/authcode-flow-user#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5%20access_token">документации</a>
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class VKResponseDTO {
    /**
     * Токен доступа, который можно использовать для вызова VK API
     */
    private String access_token;

    /**
     * Время жизни ключа в секундах
     */
    private String expires_in;

    /**
     * User ID авторизованного пользователя
     */
    private String user_id;
}
