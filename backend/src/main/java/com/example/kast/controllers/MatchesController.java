package com.example.kast.controllers;


import com.example.kast.controllers.dto.matches.CreateMatchDTO;
import com.example.kast.controllers.dto.matches.EditMatchInfoDTO;
import com.example.kast.controllers.dto.matches.FullMatchesDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.embedded.Matches;
import com.example.kast.services.MatchesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


/**
 * Данный REST-контроллер отвечает за обработку запросов, связанных со страницей "Матчи"
 *
 * @param matchesService объект класса {@link MatchesService} - сервис, обрабатывающий запросы, приходящие со страницы "Матчи"
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record MatchesController(MatchesService matchesService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/getFullMatches/{player}". Используется для получения информации,
     * необходимой для взаимодействия со страницей "Матчи" на frontend
     *
     * @param player ник игрока, который запрашивает информацию
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link FullMatchesDTO},
     * содержащий информацию, необходимую для взаимодействия со страницей "Матчи" на frontend
     */
    @GetMapping(value = "/getFullMatches/{player}")
    public ResponseEntity<FullMatchesDTO> getFullMatches(@PathVariable("player") String player) {
        return ResponseEntity.ok(matchesService.getFullMatches(player));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editMatchInfo/{event}/{matchId}". Используется при изменении информации
     * о матче
     *
     * @param event            название турнира, в рамках которого проходит матч
     * @param matchId          ID матча, для которого изменяется информация
     * @param editMatchInfoDTO объект класса {@link EditMatchInfoDTO}, содержащий измененную информацию о выбранном матче
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link EditMatchInfoDTO},
     * содержащий измененную информацию о выбранном матче
     * @throws AppException Если в базе данных не существует турнира с таким названием или команды с соответствующим
     *                      названием или тэгом
     */
    @PostMapping(value = "/editMatchInfo/{event}/{matchId}")
    public ResponseEntity<EditMatchInfoDTO> editMatchInfo(@RequestBody EditMatchInfoDTO editMatchInfoDTO,
                                                          @PathVariable("matchId") int matchId,
                                                          @PathVariable("event") String event) throws AppException {
        return ResponseEntity.ok(matchesService.editMatchInfo(editMatchInfoDTO, matchId, event));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/deleteMatch/{event}/{id}". Используется при удалении матча
     *
     * @param event   название турнира, в рамках которого проходит матч
     * @param matchId ID матча, который необходимо удалить
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link Matches}, содержащий
     * информацию об удаленном матче
     * @throws AppException Если в базе данных не существует турнира с таким названием
     */
    @PostMapping(value = "/deleteMatch/{event}/{matchId}")
    public ResponseEntity<Matches> deleteMatch(@PathVariable("matchId") int matchId,
                                               @PathVariable("event") String event) throws AppException {
        return ResponseEntity.ok(matchesService.deleteMatch(matchId, event));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/createMatch". Используется при создании матча
     *
     * @param createMatchDTO объект класса {@link CreateMatchDTO}, содержащий информацию о новом матче
     * @return <code>ResponseEntity</code> со статусом 201, тело которого - объект класса {@link Matches}, содержащий
     * информацию о созданном матче
     * @throws AppException Если в базе данных не существует турнира с таким названием или команды с соответствующим
     *                      названием или тэгом
     */
    @PostMapping(value = "/createMatch")
    public ResponseEntity<Matches> createMatch(@RequestBody CreateMatchDTO createMatchDTO) throws AppException {
        Matches createdMatch = matchesService.createMatch(createMatchDTO);
        return ResponseEntity.created(URI.create("/" + createdMatch.getMatchId())).body(createdMatch);
    }
}
