package com.example.kast.mongo_collections.interfaces;


import com.example.kast.mongo_collections.documents.MapPoolDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Интерфейс репозитория, работающий с сущностями {@link MapPoolDoc}. Является расширением {@link MongoRepository}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Repository
public interface MapPoolRepository extends MongoRepository<MapPoolDoc, String> {
    /**
     * Метод позволяет получить объект класса {@link MapPoolDoc}, содержащий информацию о карте из списка доступных карт
     * по внутреннему имени карты для обычных игр
     *
     * @param internalName внутреннее имя карты для обычных игр
     * @return Объект класса {@link MapPoolDoc}, содержащий информацию о карте из списка доступных карт
     */
    MapPoolDoc findByInternalName(final String internalName);


    /**
     * Метод позволяет получить объект класса {@link MapPoolDoc}, содержащий информацию о карте из списка доступных карт
     * по внутреннему имени карты для напарников
     *
     * @param internalNameWingman внутреннее имя карты для напарников
     * @return Объект класса {@link MapPoolDoc}, содержащий информацию о карте из списка доступных карт
     */
    MapPoolDoc findByInternalNameWingman(final String internalNameWingman);


    /**
     * Метод позволяет определить, находится ли в базе данных карта с соответствующим внутренним именем для обычных игр
     *
     * @param internalName внутреннее имя карты для обычных игр
     * @return Находится ли в базе данных карта с соответствующим внутренним именем для обычных игр: <code>true</code>,
     * если да; <code>false</code> иначе
     */
    boolean existsByInternalName(final String internalName);


    /**
     * Метод позволяет определить, находится ли в базе данных карта с соответствующим внутренним именем для напарников
     *
     * @param internalNameWingman внутреннее имя карты для напарников
     * @return Находится ли в базе данных карта с соответствующим внутренним именем для напарников: <code>true</code>,
     * если да; <code>false</code> иначе
     */
    boolean existsByInternalNameWingman(final String internalNameWingman);
}
