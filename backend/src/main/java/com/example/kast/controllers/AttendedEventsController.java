package com.example.kast.controllers;


import com.example.kast.controllers.dto.tab.AttendedEventDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.services.AttendedEventsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


/**
 * Данный REST-контроллер отвечает за обработку запросов для получения всех турниров,
 * в которых принимал участие игрок или команда
 *
 * @param attendedEventsService объект класса {@link AttendedEventsService} - сервис, обрабатывающий запросы получения
 *                              посещенных турниров
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record AttendedEventsController(AttendedEventsService attendedEventsService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/getPlayerAttendedEvents/{player}" для получения всех турниров,
     * посещенных игроком
     *
     * @param player ник игрока, для которого необходимо получить посещенные турниры
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса {@link AttendedEventDTO},
     * содержащих информацию о всех турнирах, посещенных игроком
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    @GetMapping(value = "/getPlayerAttendedEvents/{player}")
    public ResponseEntity<ArrayList<AttendedEventDTO>> getPlayerAttendedEvents(@PathVariable("player") String player) throws AppException {
        return ResponseEntity.ok(attendedEventsService.getPlayerAttendedEvents(player));
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getTeamAttendedEvents/{team}" для получения всех турниров,
     * посещенных командой
     *
     * @param team название команды, для которой необходимо получить посещенные турниры
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса {@link AttendedEventDTO},
     * содержащих информацию о всех турнирах, посещенных командой
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    @GetMapping(value = "/getTeamAttendedEvents/{team}")
    public ResponseEntity<ArrayList<AttendedEventDTO>> geTeamAttendedEvents(@PathVariable("team") String team) throws AppException {
        return ResponseEntity.ok(attendedEventsService.getTeamAttendedEvents(team));
    }
}
