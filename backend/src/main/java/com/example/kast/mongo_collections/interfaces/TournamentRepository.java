package com.example.kast.mongo_collections.interfaces;


import com.example.kast.mongo_collections.documents.TournamentDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Интерфейс репозитория, работающий с сущностями {@link TournamentDoc}. Является расширением {@link MongoRepository}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Repository
public interface TournamentRepository extends MongoRepository<TournamentDoc, String> {
    /**
     * Метод позволяет получить объект класса {@link TournamentDoc}, содержащий информацию о турнире с соответствующим
     * названием
     *
     * @param name название турнира, о котором необходимо получить информацию
     * @return Объект класса {@link TournamentDoc}, содержащий информацию о турнире с соответствующим названием
     */
    TournamentDoc findByName(final String name);


    /**
     * Метод позволяет удалить турнир из базы данных
     *
     * @param name название турнира, который необходимо удалить из списка турниров
     */
    void deleteByName(final String name);


    /**
     * Метод позволяет определить, существует ли в базе данных турнир с соответствующим названием
     *
     * @param name название турнира, наличие которого необходимо проверить
     * @return Существует ли в базе данных турнир с соответствующим названием: <code>true</code>, если да;
     * <code>false</code> иначе
     */
    boolean existsByName(final String name);
}
