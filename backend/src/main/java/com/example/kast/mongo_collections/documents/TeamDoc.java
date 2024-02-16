package com.example.kast.mongo_collections.documents;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Данный класс описывает сущность <code>Team</code> из базы данных MongoDB. Содержит информацию о команде
 *
 * @author Кирилл "Tamada" Симовин, Александр "ugly4" Федякин
 */
@Document("Team")
@Data
@AllArgsConstructor
public class TeamDoc {
    /**
     * Название команды. Идентификатор
     */
    @Id
    private String teamName;

    /**
     * Тэг команды
     */
    private String tag;

    /**
     * Капитан команды
     */
    private String captain;

    /**
     * Описание команды
     */
    private String description;

    /**
     * Страна команды
     */
    private String country;

    /**
     * Город команды. Может быть пустой строкой
     */
    private String city;

    /**
     * Путь до логотипа команды на сервере
     */
    private String logoLink;

    /**
     * Позиция команды в топе. Изначально принимает значение -999
     */
    private Integer top;

    /**
     * На сколько мест изменилась позиция команды в топе. Изначально принимает значение -999
     */
    private Integer topDiff;
}
