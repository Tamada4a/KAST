package com.example.kast.mongo_collections.documents;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;


/**
 * Данный класс описывает сущность <code></code> из базы данных MongoDB. Содержит информацию о стране
 *
 * @author Кирилл "Tamada" Симовин, Александр "ugly4" Федякин
 */
@Document("Country")
@Data
public class CountryDoc {
    /**
     * Название страны на русском языке. Идентификатор
     */
    @Id
    private String countryRU;

    /**
     * Название страны на английском
     */
    private String countryENG;

    /**
     * Список городов страны
     */
    private ArrayList<String> cities;

    /**
     * Путь до маленького флага страны на frontend
     */
    private String flagPathMini;

    /**
     * Путь до большого флага страны на frontend
     */
    private String flagPath;


    /**
     * Инициализирует и создает новый объект класса CountryDoc
     *
     * @param countryRU  название страны на русском языке
     * @param countryENG название страны на английском
     * @param cities     список городов страны
     */
    public CountryDoc(String countryRU, String countryENG, ArrayList<String> cities) {
        this.countryRU = countryRU;
        this.countryENG = countryENG;
        this.cities = cities;
        this.flagPathMini = String.format("../../img/flags/mini/%s.svg", countryENG);
        this.flagPath = String.format("../../img/flags/max/%s.svg", countryENG);
    }
}
