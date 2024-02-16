package com.example.kast.mongo_collections.interfaces;


import com.example.kast.mongo_collections.documents.CountryDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Интерфейс репозитория, работающий с сущностями {@link CountryDoc}. Является расширением {@link MongoRepository}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Repository
public interface CountryRepository extends MongoRepository<CountryDoc, String> {
    /**
     * Метод позволяет получить объект класса {@link CountryDoc}, содержащий информацию о соответствующей стране
     *
     * @param countryRU название страны на русском языке, о которой необходимо получить информацию
     * @return Объект класса {@link CountryDoc}, содержащий информацию о соответствующей стране
     */
    CountryDoc findByCountryRU(final String countryRU);
}
