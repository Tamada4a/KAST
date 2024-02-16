package com.example.kast.mongo_collections.documents;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Данный класс описывает сущность <code>Top</code> из базы данных MongoDB. Содержит информацию о дате изменения топа
 *
 * @author Кирилл "Tamada" Симовин
 */
@Document("Top")
@Data
@AllArgsConstructor
public class TopDoc {
    /**
     * Дата изменения топа. Идентификатор
     */
    @Id
    private String topDate;
}
