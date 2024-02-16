package com.example.kast.controllers;


import com.example.kast.controllers.dto.matches.MatchTimeByDateDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.services.ResultsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


/**
 * Данный REST-контроллер отвечает за обработку запросов, связанных с результатами команд и игроков
 *
 * @param resultsService объект класса {@link ResultsService} - сервис, обрабатывающий запросы, связанные с получением
 *                       результатов команд и игроков
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record ResultsController(ResultsService resultsService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/getAllResults". Используется для получения результатов всех матчей
     *
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса
     * {@link MatchTimeByDateDTO}, содержащих информацию о прошедших матчах, соответствующих определенной дате
     */
    @GetMapping(value = "/getAllResults")
    public ResponseEntity<ArrayList<MatchTimeByDateDTO>> getAllResults() {
        return ResponseEntity.ok(resultsService.getAllResults());
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getPlayerResults/{player}". Используется для получения результатов всех
     * матчей пользователя
     *
     * @param player ник игрока, чьи результаты необходимо получить
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса
     * {@link MatchTimeByDateDTO}, содержащих информацию о прошедших матчах игрока, соответствующих определенной дате
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    @GetMapping(value = "/getPlayerResults/{player}")
    public ResponseEntity<ArrayList<MatchTimeByDateDTO>> getPlayerResults(@PathVariable("player") String player) throws AppException {
        return ResponseEntity.ok(resultsService.getPlayerResults(player));
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getTeamResults/{team}". Используется для получения результатов всех
     * матчей команды
     *
     * @param team название команды, чьи результаты необходимо получить
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса
     * {@link MatchTimeByDateDTO}, содержащих информацию о прошедших матчах команды, соответствующих определенной дате
     */
    @GetMapping(value = "/getTeamResults/{team}")
    public ResponseEntity<ArrayList<MatchTimeByDateDTO>> getTeamResults(@PathVariable("team") String team) {
        return ResponseEntity.ok(resultsService.getTeamResults(team));
    }
}
