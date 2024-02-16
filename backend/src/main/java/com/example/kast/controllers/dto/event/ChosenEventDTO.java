package com.example.kast.controllers.dto.event;


import com.example.kast.controllers.dto.tournaments.EndedEventDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;


/**
 * Класс описывает основную информацию на странице турнира. Является расширением {@link EndedEventDTO}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
@NoArgsConstructor
public class ChosenEventDTO extends EndedEventDTO {
    /**
     * Статус турнира: upcoming (еще не начался), ongoing (идет), ended (закончился)
     */
    private String status;

    /**
     * Тип турнира: игроки (1x1) или команда (2x2/5x5)
     */
    private String partType;

    /**
     * Ссылка на диск с фотографиями с турнира
     */
    private String yaDiskUrl;

    /**
     * Список объектов класса {@link ParticipantDTO} - участников турнира
     */
    private ArrayList<ParticipantDTO> participants;

    /**
     * Список объектов класса {@link TeamNickDTO} - игроков команд-участниц
     */
    private ArrayList<TeamNickDTO> teamsPlayers;

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
     * Список объектов класса {@link EventPrizePlaceDTO} - призовых мест турнира
     */
    private ArrayList<EventPrizePlaceDTO> prizePlaces;

    /**
     * Маппул турнира
     */
    private ArrayList<String> mapPool;


    /**
     * Инициализирует и создает новый объект класса ChosenEventDTO
     *
     * @param event        название турнира
     * @param flagPath     путь до флага страны проведения турнира на frontend
     * @param country      страна проведения турнира
     * @param city         город проведения турнира
     * @param date         дата проведения турнира в формате дд.мм.гггг - дд.мм.гггг
     * @param format       формат проведения турнира:
     *                     <li>1x1</li>
     *                     <li>2x2</li>
     *                     <li>5x5</li>
     * @param type         тип проведения турнира:
     *                     <li>Lan</li>
     *                     <li>Online</li>
     * @param registred    число зарегистрированных команд
     * @param total        максимальное число команд-участниц
     * @param fee          взнос с команды для участия в турнире
     * @param prize        призовой фонд турнира
     * @param status       статус турнира: upcoming (еще не начался), ongoing (идет), ended (закончился)
     * @param partType     тип турнира: игроки (1x1) или команда (2x2/5x5)
     * @param yaDiskUrl    ссылка на диск с фотографиями с турнира
     * @param participants список объектов класса {@link ParticipantDTO} - участников турнира
     * @param teamsPlayers список объектов класса {@link TeamNickDTO} - игроков команд-участниц
     * @param headerFile   ссылка на хедер турнира на сервере
     * @param eventFile    ссылка на логотип турнира на сервере
     * @param trophyFile   ссылка на изображение трофея турнира на сервере
     * @param mvpFile      ссылка на изображение MVP турнира на сервере
     * @param description  описание турнира
     * @param prizePlaces  список объектов класса {@link EventPrizePlaceDTO} - призовых мест турнира
     * @param mapPool      маппул турнира
     */
    public ChosenEventDTO(String event, String flagPath, String country, String city, String date, String format,
                          String type, Integer registred, Integer total, String fee, String prize, String status,
                          String partType, String yaDiskUrl, ArrayList<ParticipantDTO> participants,
                          ArrayList<TeamNickDTO> teamsPlayers, String headerFile, String eventFile, String trophyFile,
                          String mvpFile, String description, ArrayList<EventPrizePlaceDTO> prizePlaces,
                          ArrayList<String> mapPool) {
        this.setEvent(event);
        this.setFlagPath(flagPath);
        this.setCountry(country);
        this.setCity(city);
        this.setDate(date);
        this.setFormat(format);
        this.setType(type);
        this.setRegistred(registred);
        this.setTotal(total);
        this.setFee(fee);
        this.setPrize(prize);
        this.status = status;
        this.partType = partType;
        this.yaDiskUrl = yaDiskUrl;
        this.participants = participants;
        this.teamsPlayers = teamsPlayers;
        this.headerFile = headerFile;
        this.eventFile = eventFile;
        this.trophyFile = trophyFile;
        this.mvpFile = mvpFile;
        this.description = description;
        this.prizePlaces = prizePlaces;
        this.mapPool = mapPool;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link ChosenEventDTO}
     *
     * @return Строковое представление объекта класса {@link ChosenEventDTO}
     */
    @Override
    public String toString() {
        return String.format("ChosenEventDTO(event=%s, flagPath=%s, country=%s, city=%s, date=%s, format=%s, " +
                        "type=%s, registred=%d, total=%d, fee=%s, prize=%s, status=%s, partType=%s, yaDiskUrl=%s, participants=%s, " +
                        "teamsPlayers=%s, headerFile=%s, eventFile=%s, trophyFile=%s, mvpFile=%s, description=%s, prizePlaces=%s, " +
                        "mapPool=%s)", this.getEvent(), this.getFlagPath(), this.getCountry(), this.getCity(), this.getDate(),
                this.getFormat(), this.getType(), this.getRegistred(), this.getTotal(), this.getFee(), this.getPrize(),
                status, partType, yaDiskUrl, participants.toString(), teamsPlayers.toString(), headerFile, eventFile,
                trophyFile, mvpFile, description, prizePlaces.toString(), mapPool.toString());
    }
}
