package com.example.kast.mongo_collections.documents;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Данный класс описывает сущность <code>Weapon</code> из базы данных MongoDB. Содержит название оружия и его тип
 *
 * @author Кирилл "Tamada" Симовин
 */
@Document("Weapon")
@Getter
@Setter
@AllArgsConstructor
public class WeaponDoc {
    /**
     * Название оружия. Идентификатор
     */
    @Id
    private String name;

    /**
     * Тип оружия:
     * <li><b>pistol</b> - пистолет</li>
     * <li><b>other</b> - все остальное</li>
     */
    private String type;
}
