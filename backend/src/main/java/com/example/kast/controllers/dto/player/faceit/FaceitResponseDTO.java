package com.example.kast.controllers.dto.player.faceit;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс описывает ответ на запрос к Faceit API для получения токена.<br></br>
 * Подробнее в <a href="https://cdn.faceit.com/third_party/docs/FACEIT_Connect_3.0.pdf">документации</a>
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class FaceitResponseDTO {
    /**
     * Токен доступа, который можно использовать для вызова Faceit API
     */
    private String access_token;

    /**
     * Тип токена. Ожидается, что он будет Bearer
     */
    private String token_type;

    /**
     * Токен обновления, который нужно использовать для обновления токена доступа по истечении его срока действия
     */
    private String refresh_token;

    /**
     * Время в секундах, которое код будет действительным
     */
    private Integer expires_in;

    /**
     * Области, на использование которых авторизован этот токен доступа:
     * <li>email</li>
     * <li>membership</li>
     * <li>openid</li>
     * <li>profile</li>
     */
    private String scope;

    /**
     * Запрошенные пользовательские данные в формате JWT
     */
    private String id_token;
}
