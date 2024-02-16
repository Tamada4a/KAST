package com.example.kast.controllers;


import com.example.kast.controllers.dto.match.*;
import com.example.kast.controllers.dto.match.map.LogDTO;
import com.example.kast.controllers.dto.match.map.MapDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.services.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


/**
 * Данный REST-контроллер отвечает за обработку запросов, связанных со страницей матча
 *
 * @param matchService объект класса {@link MatchService} - сервис, обрабатывающий запросы, приходящие со страницы матча
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record MatchController(MatchService matchService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/getFullMatch/{matchId}/{player}". Используется для получения необходимой
     * информации для взаимодействия со страницей матча на frontend
     *
     * @param matchId ID матча, для которого запрашивается информация
     * @param player  ник игрока, который запрашивает информацию
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link FullMatchDTO},
     * содержащий информацию для взаимодействия со страницей матча на frontend
     * @throws AppException Если в базе данных не существует матча с таким ID
     */
    @GetMapping(value = "/getFullMatch/{matchId}/{player}")
    public ResponseEntity<FullMatchDTO> getFullMatch(@PathVariable("matchId") int matchId,
                                                     @PathVariable("player") String player) throws AppException {
        return ResponseEntity.ok(matchService.getFullMatch(matchId, player));
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getMatchStreams/{matchId}". Используется получения списка стримов
     * выбранного матча
     *
     * @param matchId ID матча, для которого запрашивается информация
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса {@link StreamFlagDTO},
     * содержащих информацию о прямых трансляциях, освещающих выбранный матч
     * @throws AppException Если в базе данных не существует матча с таким ID
     */
    @GetMapping(value = "/getMatchStreams/{matchId}")
    public ResponseEntity<ArrayList<StreamFlagDTO>> getMatchStreams(@PathVariable("matchId") int matchId) throws AppException {
        return ResponseEntity.ok(matchService.getMatchStreams(matchId));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editMatchDesc/{matchId}". Используется при редактировании информации о
     * матче - описании, пиках/банах
     *
     * @param matchId        ID матча, для которого редактируется информации
     * @param chosenMatchDTO объект класса {@link ChosenMatchDTO}, содержащий измененную информацию о выбранном матче
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link ChosenMatchDTO},
     * содержащий всю информацию о выбранном матче
     * @throws AppException Если в базе данных не существует матча с таким ID
     */
    @PostMapping(value = "/editMatchDesc/{matchId}")
    public ResponseEntity<ChosenMatchDTO> editMatchDesc(@PathVariable("matchId") int matchId,
                                                        @RequestBody ChosenMatchDTO chosenMatchDTO) throws AppException {
        return ResponseEntity.ok(matchService.editMatchDesc(matchId, chosenMatchDTO));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editMatchStreams/{event}/{matchId}". Используется при редактировании
     * прямых трансляций, освещающих матч или при изменении IP-адреса сервера, на котором проходит матч
     *
     * @param matchId             ID матча, для которого изменяется список стримов и IP-адрес
     * @param event               название турнира, в рамках которого проходит матч
     * @param editMatchStreamsDTO объект класса {@link EditMatchStreamsDTO}, содержащий информацию об измененных стримах
     *                            и IP-адресе сервера
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса {@link StreamFlagDTO},
     * содержащих информацию об измененных стримах, освещающих выбранный матч
     * @throws AppException Если турнира с таким названием не существует в базе данных или матча с запрашиваемым ID
     */
    @PostMapping(value = "/editMatchStreams/{event}/{matchId}")
    public ResponseEntity<ArrayList<StreamFlagDTO>> editMatchStreams(@PathVariable("event") String event,
                                                                     @PathVariable("matchId") int matchId,
                                                                     @RequestBody EditMatchStreamsDTO editMatchStreamsDTO) throws AppException {
        return ResponseEntity.ok(matchService.editMatchStreams(event, matchId, editMatchStreamsDTO));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/parseLogs/{event}/{matchId}". Используется при парсинге входящих логов
     * (событий, происходящих на сервере) с сервера, на котором проходит выбранный матч
     *
     * @param event   название турнира, в рамках которого проходит матч
     * @param matchId ID матча, для которого парсятся логи
     * @param input   входящие логи
     * @throws AppException Если в базе данных не существует: турнира с таким названием; матча с таким ID; команды с
     *                      таким названием
     */
    @PostMapping(value = "/parseLogs/{event}/{matchId}")
    public void parseLogs(@RequestBody String input,
                          @PathVariable("event") String event,
                          @PathVariable("matchId") int matchId) throws AppException {
        matchService.parseLogs(input, event, matchId);
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getLogs/{event}/{matchId}". Используется для получения логов выбранного
     * матча
     *
     * @param matchId ID матча, для которого запрашивается информация
     * @param event   название турнира, в рамках которого проходит матч
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса {@link LogDTO},
     * содержащих информацию о логах, приходящих с сервера
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    @GetMapping(value = "/getLogs/{event}/{matchId}")
    public ResponseEntity<ArrayList<LogDTO>> getLogs(@PathVariable("event") String event,
                                                     @PathVariable("matchId") int matchId) throws AppException {
        return ResponseEntity.ok(matchService.getLogs(event, matchId));
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getCurrentMap/{event}/{matchId}". Используется в списке логов на frontend
     * для получения карты, играемой на сервере в данный момент с целью установки соответствующего фона
     *
     * @param event   название турнира, в рамках которого проходит матч
     * @param matchId ID матча, для которого запрашивается информация
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - название активной карты. Может быть
     * <code>TBA</code>, если ни одна карта не играется
     */
    @GetMapping(value = "/getCurrentMap/{event}/{matchId}")
    public ResponseEntity<String> getCurrentMap(@PathVariable("event") String event,
                                                @PathVariable("matchId") int matchId) {
        MapDTO currentMap = matchService.getCurrentMap(event, matchId);
        if (currentMap == null)
            return ResponseEntity.ok("TBA");
        return ResponseEntity.ok(currentMap.getMapName());
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getScoreboard/{event}/{matchId}". Используется для получения информации,
     * отображаемой в таблице (scoreboard) на странице матча
     *
     * @param matchId ID матча, для которого запрашивается информация
     * @param event   название турнира, в рамках которого проходит матч
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link ScoreBoardDTO},
     * описывающий таблицу на странице матча
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    @GetMapping(value = "/getScoreboard/{event}/{matchId}")
    public ResponseEntity<ScoreBoardDTO> getScoreboard(@PathVariable("event") String event,
                                                       @PathVariable("matchId") int matchId) throws AppException {
        return ResponseEntity.ok(matchService.getScoreboard(event, matchId));
    }
}

