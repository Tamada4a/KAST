package com.example.kast.mongo_collections.interfaces;


import com.example.kast.mongo_collections.documents.TeamDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Интерфейс репозитория, работающий с сущностями {@link TeamDoc}. Является расширением {@link MongoRepository}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Repository
public interface TeamRepository extends MongoRepository<TeamDoc, String> {
    /**
     * Метод позволяет получить объект класса {@link TeamDoc}, содержащий информацию о команде с соответствующим
     * названием команды
     *
     * @param teamName название команды, о которой необходимо получить информацию
     * @return Объект класса {@link TeamDoc}, содержащий информацию о команде
     */
    TeamDoc findByTeamName(final String teamName);


    /**
     * Метод позволяет получить объект класса {@link TeamDoc}, содержащий информацию о команде с соответствующим
     * тэгом команды
     *
     * @param tag тэг команды, о которой необходимо получить информацию
     * @return Объект класса {@link TeamDoc}, содержащий информацию о команде
     */
    TeamDoc findByTag(final String tag);


    /**
     * Метод позволяет определить, существует ли в базе данных команда с соответствующим названием
     *
     * @param teamName название команды, наличие которой необходимо проверить
     * @return Существует ли в базе данных команда с соответствующим названием: <code>true</code>, если да;
     * <code>false</code> иначе
     */
    boolean existsByTeamName(final String teamName);


    /**
     * Метод позволяет определить, существует ли в базе данных команда с соответствующим тэгом
     *
     * @param tag тэг команды, наличие которой необходимо проверить
     * @return Существует ли в базе данных команда с соответствующим названием: <code>true</code>, если да;
     * <code>false</code> иначе
     */
    boolean existsByTag(final String tag);
}
