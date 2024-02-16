package com.example.kast.mongo_collections.documents;


import com.example.kast.controllers.dto.other.NotificationsDTO;
import com.example.kast.mongo_collections.embedded.PlayerFullStats;
import com.example.kast.mongo_collections.embedded.Rosters;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import static com.example.kast.utils.Utils.fixNumber;


/**
 * Данный класс описывает сущность <code>Player</code> из базы данных MongoDB. Содержит информацию о пользователе
 *
 * @author Кирилл "Tamada" Симовин, Александр "ugly4" Федякин
 */
@Document("Player")
@Getter
@Setter
public class PlayerDoc {
    /**
     * ID пользователя. Идентификатор
     */
    @Id
    private Integer userID;

    /**
     * Ник пользователя
     */
    private String nick;

    /**
     * Старый ник пользователя. Изначально - пустая строка. Принимает отличное от пустой строки значение после смены
     * ника пользователя
     */
    private String oldNick;

    /**
     * Пароль пользователя. Хранится в хэшированном виде
     */
    private String password;

    /**
     * Имя пользователя
     */
    private String firstName;

    /**
     * Фамилия пользователя
     */
    private String secondName;

    /**
     * Дата рождения пользователя
     */
    private LocalDate bdate;

    /**
     * Страна пользователя
     */
    private String country;

    /**
     * Ссылка на профиль пользователя в Steam
     */
    private String steam;

    /**
     * Ссылка на профиль пользователя на Faceit
     */
    private String faceit;

    /**
     * Ник пользователя в Discord
     */
    private String discord;

    /**
     * Ссылка на профиль пользователя во ВКонтакте
     */
    private String vk;

    /**
     * Путь до фотографии пользователя на сервере
     */
    private String photoLink;

    /**
     * Почта пользователя
     */
    private String email;

    /**
     * Список личных достижений игрока
     */
    private ArrayList<String> trophies;

    /**
     * Объект класса {@link PlayerFullStats}, содержащий полную статистику игрока по всем сыгранным матча
     */
    private PlayerFullStats stats;

    /**
     * Список объектов класса {@link Rosters}, содержащих информацию о времени пребывания пользователя в командах
     */
    private ArrayList<Rosters> rosters;

    /**
     * Список объектов класса {@link NotificationsDTO}, содержащих информацию об уведомлениях пользователя
     */
    private ArrayList<NotificationsDTO> notifications;


    /**
     * Инициализирует и создает новый объект класса PlayerDoc
     *
     * @param nick       ник пользователя
     * @param country    страна пользователя
     * @param email      почта пользователя
     * @param firstName  имя пользователя
     * @param secondName фамилия пользователя
     * @param userID     ID пользователя
     */
    public PlayerDoc(Integer userID, String nick, String firstName, String secondName, String country, String email) {
        this.userID = userID;
        this.password = "";
        this.nick = nick;
        this.firstName = firstName;
        this.secondName = secondName;
        this.bdate = null;
        this.country = country;
        this.steam = "";
        this.faceit = "";
        this.discord = "";
        this.vk = "";
        this.email = email;
        this.photoLink = "/players/NonPhoto.png";
        this.trophies = new ArrayList<>();
        this.stats = new PlayerFullStats();
        this.rosters = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link PlayerDoc}
     *
     * @return Строковое представление объекта класса {@link PlayerDoc}
     */
    @Override
    public String toString() {
        String day = "";
        String month = "";
        String year = "";
        if (bdate != null) {
            day = fixNumber(bdate.getDayOfMonth());
            month = fixNumber(bdate.getMonthValue());
            year = String.valueOf(bdate.getYear());
        }

        return String.format("Player(userID=%d, password=%s, nick=%s, firstName=%s, secondName=%s, bdate=%s-%s-%s, country=%s, " +
                        "steam=%s, faceit=%s, discord=%s, vk=%s, " +
                        "photoLink=%s, email=%s, trophies=%s, stats=%s, rosters=%s, notifications=%s)",
                userID, password, nick, firstName, secondName, day, month, year, country, steam, faceit, discord,
                vk, photoLink, email,
                trophies.toString(), stats.toString(), rosters.toString(), notifications.toString());
    }
}
