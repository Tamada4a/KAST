package com.example.kast.mongo_collections.interfaces;


import com.example.kast.mongo_collections.documents.WeaponDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Интерфейс репозитория, работающий с сущностями {@link WeaponDoc}. Является расширением {@link MongoRepository}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Repository
public interface WeaponRepository extends MongoRepository<WeaponDoc, String> {
    /**
     * Метод позволяет получить объект класса {@link WeaponDoc}, содержащий информацию об оружии с запрашиваемым
     * названием
     *
     * @param name название оружия, о котором необходимо получить информацию
     * @return Объект класса {@link WeaponDoc}, содержащий информацию об оружии с соответствующим названием
     */
    WeaponDoc findByName(final String name);


    /**
     * Метод позволяет определить, существует ли в базе данных оружие с соответствующим названием
     *
     * @param name название оружия, наличие которого необходимо проверить
     * @return Существует ли в базе данных оружие с соответствующим названием: <code>true</code>, если да;
     * <code>false</code> иначе
     */
    boolean existsByName(final String name);
}
