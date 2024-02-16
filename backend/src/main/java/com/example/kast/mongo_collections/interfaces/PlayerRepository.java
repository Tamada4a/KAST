package com.example.kast.mongo_collections.interfaces;


import com.example.kast.mongo_collections.documents.PlayerDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Интерфейс репозитория, работающий с сущностями {@link PlayerDoc}. Является расширением {@link MongoRepository}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Repository
public interface PlayerRepository extends MongoRepository<PlayerDoc, String> {
    /**
     * Метод позволяет получить объект класса {@link PlayerDoc}, содержащий информацию о пользователе по нику
     * пользователя
     *
     * @param nick ник пользователя, о котором необходимо получить информацию
     * @return Объект класса {@link PlayerDoc}, содержащий информацию о пользователе
     */
    PlayerDoc findByNick(final String nick);


    /**
     * Метод позволяет получить объект класса {@link PlayerDoc}, содержащий информацию о пользователе по старому нику
     * пользователя
     *
     * @param oldNick старый ник пользователя, о котором необходимо получить информацию
     * @return Объект класса {@link PlayerDoc}, содержащий информацию о пользователе
     */
    PlayerDoc findByOldNick(final String oldNick);


    /**
     * Метод позволяет определить, существует ли в базе данных пользователь с соответствующим ником
     *
     * @param nick ник пользователя, наличие которого необходимо проверить в базе данных
     * @return Существует ли в базе данных пользователь с соответствующим ником: <code>true</code>, если да;
     * <code>false</code> иначе
     */
    boolean existsByNick(final String nick);


    /**
     * Метод позволяет определить, существует ли в базе данных пользователь с соответствующим старым ником
     *
     * @param oldNick старый ник пользователя, наличие которого необходимо проверить в базе данных
     * @return Существует ли в базе данных пользователь с соответствующим старым ником: <code>true</code>, если да;
     * <code>false</code> иначе
     */
    boolean existsByOldNick(final String oldNick);


    /**
     * Метод позволяет определить, существует ли в базе данных пользователь с соответствующей ссылкой на профиль Steam
     *
     * @param steam ссылка на Steam-профиль пользователя, наличие которого необходимо проверить в базе данных
     * @return Существует ли в базе данных пользователь с соответствующей ссылкой на профиль Steam: <code>true</code>,
     * если да; <code>false</code> иначе
     */
    boolean existsBySteam(final String steam);


    /**
     * Метод позволяет определить, существует ли в базе данных пользователь с соответствующей ссылкой на профиль Faceit
     *
     * @param faceit ссылка на Faceit-профиль пользователя, наличие которого необходимо проверить в базе данных
     * @return Существует ли в базе данных пользователь с соответствующей ссылкой на профиль Faceit: <code>true</code>,
     * если да; <code>false</code> иначе
     */
    boolean existsByFaceit(final String faceit);


    /**
     * Метод позволяет определить, существует ли в базе данных пользователь с соответствующим ником в Discord
     *
     * @param discord Discord-ник пользователя, наличие которого необходимо проверить в базе данных
     * @return Существует ли в базе данных пользователь с соответствующим ником в Discord: <code>true</code>, если да;
     * <code>false</code> иначе
     */
    boolean existsByDiscord(final String discord);


    /**
     * Метод позволяет определить, существует ли в базе данных пользователь с соответствующей ссылкой на профиль
     * ВКонтакте
     *
     * @param vk ссылка на ВКонтакте-профиль пользователя, наличие которого необходимо проверить в базе данных
     * @return Существует ли в базе данных пользователь с соответствующей ссылкой на профиль ВКонтакте: <code>true</code>,
     * если да; <code>false</code> иначе
     */
    boolean existsByVk(final String vk);
}
