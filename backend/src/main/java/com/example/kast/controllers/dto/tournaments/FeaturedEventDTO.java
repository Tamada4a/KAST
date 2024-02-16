package com.example.kast.controllers.dto.tournaments;


import com.example.kast.mongo_collections.embedded.PrizePlaces;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;


/**
 * Класс содержит информацию о будущем турнире, отображаемую на странице турниров. Является расширением {@link EndedEventDTO}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
@NoArgsConstructor
public class FeaturedEventDTO extends EndedEventDTO {
    /**
     * Ссылка на хедер турнира на сервере
     */
    private String headerFile;

    /**
     * Ссылка на логотип турнира на сервере
     */
    private String eventFile;

    /**
     * Ссылка на изображение трофея турнира на сервере
     */
    private String trophyFile;

    /**
     * Ссылка на изображение MVP турнира на сервере
     */
    private String mvpFile;

    /**
     * Описание турнира
     */
    private String description;

    /**
     * Список объектов класса {@link PrizePlaces} - призовых мест турнира
     */
    private ArrayList<PrizePlaces> prizePlaces;

    /**
     * Маппул турнира
     */
    private ArrayList<String> mapPool;


    /**
     * Инициализирует и создает новый объект класса FeaturedEventDTO
     *
     * @param endedEventDTO объект класса {@link EndedEventDTO}, содержащий информацию о прошедшем турнире, отображаемую
     *                      на странице турниров
     * @param headerFile    ссылка на хедер турнира на сервере
     * @param eventFile     ссылка на логотип турнира на сервере
     * @param trophyFile    ссылка на изображение трофея турнира на сервере
     * @param mvpFile       ссылка на изображение MVP турнира на сервере
     * @param description   описание турнира
     * @param prizePlaces   список объектов класса {@link PrizePlaces} - призовых мест турнира
     * @param mapPool       маппул турнира
     */
    public FeaturedEventDTO(EndedEventDTO endedEventDTO, String headerFile, String eventFile, String trophyFile,
                            String mvpFile, String description, ArrayList<PrizePlaces> prizePlaces,
                            ArrayList<String> mapPool) {
        this.setEvent(endedEventDTO.getEvent());
        this.setFlagPath(endedEventDTO.getFlagPath());
        this.setCountry(endedEventDTO.getCountry());
        this.setCity(endedEventDTO.getCity());
        this.setDate(endedEventDTO.getDate());
        this.setFormat(endedEventDTO.getFormat());
        this.setType(endedEventDTO.getType());
        this.setRegistred(endedEventDTO.getRegistred());
        this.setTotal(endedEventDTO.getTotal());
        this.setFee(endedEventDTO.getFee());
        this.setPrize(endedEventDTO.getPrize());
        this.headerFile = headerFile;
        this.eventFile = eventFile;
        this.trophyFile = trophyFile;
        this.mvpFile = mvpFile;
        this.description = description;
        this.prizePlaces = prizePlaces;
        this.mapPool = mapPool;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link FeaturedEventDTO}
     *
     * @return Строковое представление объекта класса {@link FeaturedEventDTO}
     */
    @Override
    public String toString() {
        return String.format("FeaturedEventDTO(event=%s, flagPath=%s, country=%s, city=%s, date=%s, format=%s, type=%s, " +
                        "registred=%d, total=%s, fee=%s, prize=%s, headerFile=%s, eventFile=%s, trophyFile=%s, mvpFile=%s, " +
                        "description=%s, prizePlaces=%s, mapPool=%s)", this.getEvent(), this.getFlagPath(), this.getCountry(),
                this.getCity(), this.getDate(), this.getFormat(), this.getType(), this.getRegistred(), this.getTotal(),
                this.getFee(), this.getPrize(), headerFile, eventFile, trophyFile, mvpFile, description,
                prizePlaces.toString(), mapPool.toString());
    }
}
