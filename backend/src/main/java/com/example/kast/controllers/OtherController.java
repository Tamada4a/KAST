package com.example.kast.controllers;


import com.example.kast.controllers.dto.other.SearchDataDTO;
import com.example.kast.controllers.dto.other.NotificationsDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.CountryDoc;
import com.example.kast.services.OtherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * Данный REST-контроллер отвечает за обработку запросов без привязки к конкретной странице
 *
 * @param otherService объект класса {@link OtherService} - сервис, обрабатывающий запросы без привязки к конкретной
 *                     странице
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record OtherController(OtherService otherService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/country". Используется для получения списка всех стран
     *
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса {@link CountryDoc},
     * содержащих информацию о всех странах, находящихся в базе даннах
     */
    @GetMapping(value = "/country")
    public ResponseEntity<List<CountryDoc>> getAllCountries() {
        return ResponseEntity.ok(otherService.getAllCountries());
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/isAdmin/{player}". Используется для получения информации о том, является ли
     * пользователь администратором
     *
     * @param player ник пользователя, для которого проверяется, является ли он администратором
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - результат проверки, является ли пользователь
     * администратором: <code>true</code>, если является; <code>false</code> иначе
     */
    @GetMapping(value = "/isAdmin/{player}")
    public ResponseEntity<Boolean> isAdmin(@PathVariable("player") String player) {
        return ResponseEntity.ok(otherService.isAdmin(player));
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getSearchData". Используется для получения данных, среди которых будет
     * осуществляться поиск в поисковой строке, расположенной в заголовке страницы
     *
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link SearchDataDTO},
     * содержащий данные, среди которых осуществляется поиск в поисковой строке, расположенной в заголовке страницы
     */
    @GetMapping(value = "/getSearchData")
    public ResponseEntity<SearchDataDTO> getSearchData() {
        return ResponseEntity.ok(otherService.getSearchData());
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getNotifications/{player}". Используется для получения всех уведомлений
     * пользователя
     *
     * @param player ник игрока, чьи уведомления необходимо получить
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса
     * {@link NotificationsDTO}, содержащих информацию об уведомлениях пользователя
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    @GetMapping(value = "/getNotifications/{player}")
    public ResponseEntity<ArrayList<NotificationsDTO>> getNotifications(@PathVariable("player") String player) throws AppException {
        return ResponseEntity.ok(otherService.getNotifications(player));
    }
}
