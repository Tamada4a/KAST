package com.example.kast.controllers.dto.tournaments;


import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.mongo_collections.embedded.PrizePlaces;
import lombok.*;

import java.util.ArrayList;


/**
 * Класс содержит информацию о текущем турнире. Является расширением {@link EndedEventDTO}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
@NoArgsConstructor
public class OngoingEventDTO extends EndedEventDTO {
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
     * Ссылка на диск с фотографиями с турнира
     */
    private String yaDiskUrl;

    /**
     * Список объектов класса {@link NameDTO}, содержащих название команд-участниц турнира
     */
    private ArrayList<NameDTO> teams;

    /**
     * Список объектов класса {@link PrizePlaces}, содержащих информацию о занятых призовых местах на турнире
     */
    private ArrayList<PrizePlaces> prizePlaces;


    /**
     * Инициализирует и создает новый объект класса OngoingEventDTO
     *
     * @param endedEventDTO объект класса {@link EndedEventDTO}, содержащий информацию о прошедшем турнире, отображаемую
     *                      на странице турниров
     * @param headerFile    ссылка на хедер турнира на сервере
     * @param eventFile     ссылка на логотип турнира на сервере
     * @param trophyFile    ссылка на изображение трофея турнира на сервере
     * @param mvpFile       ссылка на изображение MVP турнира на сервере
     * @param yaDiskUrl     cсылка на диск с фотографиями с турнира
     * @param teams         cписок объектов класса {@link NameDTO}, содержащих название команд-участниц турнира
     * @param prizePlaces   cписок объектов класса {@link PrizePlaces}, содержащих информацию о занятых призовых местах на
     *                      турнире
     */
    public OngoingEventDTO(EndedEventDTO endedEventDTO, String headerFile, String eventFile, String trophyFile,
                           String mvpFile, String yaDiskUrl, ArrayList<NameDTO> teams, ArrayList<PrizePlaces> prizePlaces) {
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
        this.yaDiskUrl = yaDiskUrl;
        this.teams = teams;
        this.prizePlaces = prizePlaces;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link OngoingEventDTO}
     *
     * @return Строковое представление объекта класса {@link OngoingEventDTO}
     */
    @Override
    public String toString() {
        return String.format("FeaturedEventDTO(event=%s, flagPath=%s, country=%s, city=%s, date=%s, format=%s, type=%s, " +
                        "registred=%d, total=%s, fee=%s, prize=%s, headerFile=%s, eventFile=%s, trophyFile=%s, mvpFile=%s, " +
                        "yaDiskUrl=%s, teams=%s, prizePlaces=%s)", this.getEvent(), this.getFlagPath(), this.getCountry(),
                this.getCity(), this.getDate(), this.getFormat(), this.getType(), this.getRegistred(), this.getTotal(),
                this.getFee(), this.getPrize(), headerFile, eventFile, trophyFile, mvpFile, yaDiskUrl, teams,
                prizePlaces.toString());
    }
}
