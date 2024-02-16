package com.example.kast.mongo_collections.documents;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Данный класс описывает сущность <code>MapPool</code> из базы данных MongoDB. Содержит информацию о карте
 *
 * @author Кирилл "Tamada" Симовин
 */
@Document("MapPool")
@Data
@AllArgsConstructor
public class MapPoolDoc {
    /**
     * Название карты. Идентификатор
     */
    @Id
    private String name;

    /**
     * Внутреннее имя карты для обычных игр в виде de_. Может быть <code>null</code>, если не указано
     */
    private String internalName;

    /**
     * Внутреннее имя карты для напарников в виде de_. Может быть <code>null</code>, если не указано
     */
    private String internalNameWingman;
}