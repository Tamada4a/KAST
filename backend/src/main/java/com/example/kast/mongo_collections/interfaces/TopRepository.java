package com.example.kast.mongo_collections.interfaces;


import com.example.kast.mongo_collections.documents.TopDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Интерфейс репозитория, работающий с сущностями {@link TopDoc}. Является расширением {@link MongoRepository}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Repository
public interface TopRepository extends MongoRepository<TopDoc, String> {
}
