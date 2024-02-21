package com.example.kast.controllers;


import com.example.kast.controllers.dto.tournaments.FeaturedEventDTO;
import com.example.kast.controllers.dto.tournaments.FullTournamentsDTO;
import com.example.kast.controllers.dto.tournaments.OngoingEventDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.services.TournamentsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


/**
 * Данный REST-контроллер отвечает за обработку запросов, связанных со страницей "Турниры"
 *
 * @param tournamentsService объект класса {@link TournamentsService} - сервис, обрабатывающий запросы, приходящие со
 *                           страницы "Турниры"
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record TournamentsController(TournamentsService tournamentsService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/getFullTournaments/{player}". Используется для получения информации,
     * необходимой для взаимодействия со страницей "Турниры" на frontend
     *
     * @param player ник игрока, который просматривает страницу
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link FullTournamentsDTO},
     * содержащий информацию, необходимую для взаимодействия со страницей "Турниры" на frontend
     * @throws JsonProcessingException В случае ошибки при парсинге JSON-строки, представляющей список объектов,
     *                                 содержащих информацию о будущих или завершенных турнирах, соответствующих
     *                                 определенной дате
     */
    @GetMapping(value = "/getFullTournaments/{player}")
    public ResponseEntity<FullTournamentsDTO> getFullTournaments(@PathVariable("player") String player) throws JsonProcessingException {
        return ResponseEntity.ok(tournamentsService.getFullTournaments(player));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editOngoingEvent/{oldEvent}". Используется при редактировании информации
     * о текущем турнире
     *
     * @param ongoingEventDTO объект класса {@link OngoingEventDTO}, содержащий измененную информацию о текущем турнире
     * @param oldEvent        старое название турнира (может совпадать с названием после изменения). Используется для
     *                        определения турнира для изменения
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link OngoingEventDTO},
     * содержащий информацию об измененном текущем турнире
     * @throws AppException Если турнира со старым названием не существует в базе данных или новое название турнира
     *                      занято
     */
    @PostMapping(value = "/editOngoingEvent/{oldEvent}")
    public ResponseEntity<OngoingEventDTO> editOngoingEvent(@RequestBody OngoingEventDTO ongoingEventDTO,
                                                            @PathVariable("oldEvent") String oldEvent) throws AppException {
        return ResponseEntity.ok(tournamentsService.editOngoingEvent(ongoingEventDTO, oldEvent));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editFeaturedEvent/{oldEvent}". Используется при редактировании будущего
     * турнира
     *
     * @param featuredEventDTO объект класса {@link FeaturedEventDTO}, содержащий измененную информацию о будущем турнире
     * @param oldEvent         старое название турнира (может совпадать с названием после изменения). Используется для
     *                         определения турнира для изменения
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link FeaturedEventDTO},
     * содержащий информацию об измененном будущем турнире
     * @throws AppException Если турнира со старым названием не существует в базе данных или новое название турнира
     *                      занято
     */
    @PostMapping(value = "/editFeaturedEvent/{oldEvent}")
    public ResponseEntity<FeaturedEventDTO> editFeaturedEvent(@RequestBody FeaturedEventDTO featuredEventDTO,
                                                              @PathVariable("oldEvent") String oldEvent) throws AppException {
        return ResponseEntity.ok(tournamentsService.editFeaturedEvent(featuredEventDTO, oldEvent));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/createNewEvent". Используется при создании турнира
     *
     * @param newEvent объект класса {@link FeaturedEventDTO}, содержащий информацию о созданном турнире
     * @return <code>ResponseEntity</code> со статусом 201, тело которого - объект класса {@link FeaturedEventDTO},
     * содержащий информацию о новом турнире
     * @throws AppException Если название турнира занято
     */
    @PostMapping(value = "/createNewEvent")
    public ResponseEntity<TournamentDoc> createNewEvent(@RequestBody FeaturedEventDTO newEvent) throws AppException {
        TournamentDoc createdEvent = tournamentsService.createNewEvent(newEvent);
        return ResponseEntity.created(URI.create("/" + createdEvent.getName())).body(createdEvent);
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/deleteEvent/{event}". Используется при удалении турнира
     *
     * @param event название турнира, который необходимо удалить
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link TournamentDoc},
     * содержащий информацию об удаленном турнире
     * @throws AppException Если турнира со старым названием не существует в базе данных
     */
    @PostMapping(value = "/deleteEvent/{event}")
    public ResponseEntity<TournamentDoc> deleteEvent(@PathVariable("event") String event) throws AppException {
        return ResponseEntity.ok(tournamentsService.deleteEvent(event));
    }
}
