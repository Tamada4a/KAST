package com.example.kast.controllers.dto.match.twitch;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


/**
 * Класс описывает информацию о стриме
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class TwitchViewersDataDTO {
    /**
     * ID, идентифицирующий поток. Позже его можно будет использовать для просмотра видео по запросу (VOD)
     */
    private String id;

    /**
     * ID пользователя, ведущего стрим
     */
    private String user_id;

    /**
     * Имя пользователя для входа в систему
     */
    private String user_login;

    /**
     * Отображаемое имя пользователя
     */
    private String user_name;

    /**
     * ID категории или игры, которую демонстрирует стример
     */
    private String game_id;

    /**
     * Название категории или игры, которую демонстрирует стример
     */
    private String game_name;

    /**
     * Тип стрима. Может принимать значения: live. В случае ошибки данное поле будет пустым
     */
    private String type;

    /**
     * Название стрима. Если не указано, поле будет пустым
     */
    private String title;

    /**
     * Число зрителей трансляции
     */
    private Integer viewer_count;

    /**
     * Дата и время (UTC) начала стрима в формате RFC3339
     */
    private String started_at;

    /**
     * Язык трансляции
     */
    private String language;

    /**
     * URL изображения кадра из последних 5 минут трансляции
     */
    private String thumbnail_url;

    /**
     * @deprecated Список тегов стрима
     */
    private ArrayList<String> tag_ids;

    /**
     * Список тегов стрима
     */
    private ArrayList<String> tags;

    /**
     * Предназначен ли стрим для взрослой аудитории
     */
    private Boolean is_mature;
}
