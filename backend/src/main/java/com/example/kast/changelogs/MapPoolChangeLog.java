package com.example.kast.changelogs;


import com.example.kast.mongo_collections.documents.MapPoolDoc;
import com.example.kast.mongo_collections.interfaces.MapPoolRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * Данный класс содержит логику для миграции списка объектов класса {@link MapPoolDoc}. Используется для инициализации
 * соответствующей коллекции базы данных
 *
 * @author Кирилл "Tamada" Симовин
 */
@ChangeUnit(id = "MapPoolChangeLog", order = "40001", author = "Tamada")
public class MapPoolChangeLog {
    /**
     * Метод позволяет сохранить полученный список объектов класса {@link MapPoolDoc}, содержащих информацию о картах
     *
     * @param mapPoolRepository для взаимодействия с сущностями {@link MapPoolDoc}
     */
    @Execution
    public void changeSet(MapPoolRepository mapPoolRepository) {
        getMapPool().forEach(mapPoolRepository::save);
    }


    /**
     * Метод позволяет откатить внесенные изменения
     */
    @RollbackExecution
    public void rollback() {
    }


    /**
     * Метод позволяет получить список карт, из которых формируются маппулы турниров.<br></br>
     * Более подробную информацию можно узнать на
     * <a href="https://developer.valvesoftware.com/wiki/Counter-Strike_2/Maps">сайте</a> для разработчиков
     *
     * @return Список объектов класса {@link MapPoolDoc}, содержащих информацию о картах
     */
    private ArrayList<MapPoolDoc> getMapPool() {
        ArrayList<MapPoolDoc> mapPoolDocs = new ArrayList<>(Arrays.asList(
                new MapPoolDoc("Ancient", "de_ancient", null),
                new MapPoolDoc("Anubis", "de_anubis", null),
                new MapPoolDoc("Cache", "de_cache", null),
                new MapPoolDoc("Cobblestone", "de_cbble", null),
                new MapPoolDoc("Dust", "de_dust", "de_shortdust"),
                new MapPoolDoc("Dust2", "de_dust2", null),
                new MapPoolDoc("Inferno", "de_inferno", null),
                new MapPoolDoc("Mirage", "de_mirage", null),
                new MapPoolDoc("Nuke", "de_nuke", "de_shortnuke"),
                new MapPoolDoc("Overpass", "de_overpass", null),
                new MapPoolDoc("Train", "de_train", "de_shorttrain"),
                new MapPoolDoc("Vertigo", "de_vertigo", null)
        ));
        mapPoolDocs.sort(Comparator.comparing(MapPoolDoc::getName));
        return mapPoolDocs;
    }
}
