package com.example.kast.mongo_collections.documents;


import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Данный класс описывает сущность <code>Admin</code> из базы данных MongoDB. Содержит ник пользователя, являющегося
 * администратором
 *
 * @author Кирилл "Tamada" Симовин, Александр "ugly4" Федякин
 */
@Document("Admin")
@ToString
@AllArgsConstructor
public class AdminDoc {
    /**
     * Ник пользователя-администратора. Идентификатор
     */
    @Id
    private String adminId;
}
