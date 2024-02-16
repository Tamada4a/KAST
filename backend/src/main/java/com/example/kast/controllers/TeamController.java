package com.example.kast.controllers;


import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.controllers.dto.other.NotificationsDTO;
import com.example.kast.controllers.dto.tab.ChangeDescriptionDTO;
import com.example.kast.controllers.dto.team.FullTeamDTO;
import com.example.kast.controllers.dto.team.LeftTeamDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


/**
 * Данный REST-контроллер отвечает за обработку запросов, связанных со страницей команды
 *
 * @param teamService объект класса {@link TeamService} - сервис, обрабатывающий запросы, приходящие со страницы команды
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record TeamController(TeamService teamService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/getFullTeam/{teamName}/{player}". Используется для получения информации,
     * необходимой для взаимодействия со страницей команды
     *
     * @param player   ник игрока, который просматривает страницу
     * @param teamName название команды, для которой получается информация
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link FullTeamDTO},
     * содержащий информацию, необходимую для взаимодействия со страницей команды на frontend
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    @GetMapping(value = "/getFullTeam/{teamName}/{player}")
    public ResponseEntity<FullTeamDTO> getFullTeam(@PathVariable("teamName") String teamName,
                                                   @PathVariable("player") String player) throws AppException {
        return ResponseEntity.ok(teamService.getFullTeam(teamName, player));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/sendTeamInvite/{teamName}/{player}". Используется для отправки приглашения
     * в команду
     *
     * @param teamName название команды, от имени которой отправляется приглашение
     * @param player   ник игрока, который приглашается в команду
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - строка со значением "Приглашение отправлено
     * успешно"
     * @throws AppException Если пользователя с таким ником или команды с таким названием не существует в базе данных;
     *                      если у приглашаемого пользователя уже есть команда; если приглашаемому игроку было
     *                      отправлено приглашение от имени данной команды
     */
    @PostMapping(value = "/sendTeamInvite/{teamName}/{player}")
    public ResponseEntity<String> sendTeamInvite(@PathVariable("teamName") String teamName,
                                                 @PathVariable("player") String player) throws AppException {
        return ResponseEntity.ok(teamService.sendTeamInvite(teamName, player));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/inviteDecision/{teamName}/{player}/{decision}". Используется для определения
     * решения игрока по поводу приглашения в команду
     *
     * @param player   ник игрока, который приглашается в команду
     * @param teamName название команды, от имени которой отправляется приглашение
     * @param decision решение игрока на тему вступления в команду
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link NotificationsDTO},
     * содержащий информацию о решении игрока и тип уведомления
     * @throws AppException Если пользователя с таким ником или команды с таким названием не существует в базе данных;
     *                      если у приглашаемого пользователя уже есть команда
     */
    @PostMapping(value = "/inviteDecision/{teamName}/{player}/{decision}")
    public ResponseEntity<NotificationsDTO> inviteDecision(@PathVariable("teamName") String teamName,
                                                           @PathVariable("player") String player,
                                                           @PathVariable("decision") String decision) throws AppException {
        return ResponseEntity.ok(teamService.inviteDecision(teamName, player, decision));
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getPlayersWithoutTeams". Используется для получения списка свободных
     * игроков, которым потенциально можно отправить приглашение в команду
     *
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса {@link NameDTO},
     * содержащих ники игроков без команды
     */
    @GetMapping(value = "/getPlayersWithoutTeams")
    public ResponseEntity<ArrayList<NameDTO>> getPlayersWithoutTeams() {
        return ResponseEntity.ok(teamService.getPlayersWithoutTeams());
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/changeDescription". Используется для изменения описания команды
     *
     * @param descDTO объект класса {@link ChangeDescriptionDTO}, содержащий измененное описание соответствующей команды
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link ChangeDescriptionDTO},
     * содержащий измененное описание соответствующей команды
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    @PostMapping(value = "/changeDescription")
    public ResponseEntity<ChangeDescriptionDTO> changeDescription(@RequestBody ChangeDescriptionDTO descDTO) throws AppException {
        return ResponseEntity.ok(teamService.changeDescription(descDTO));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/leftTeam". Используется при выходе или исключении игрока из команды
     *
     * @param leftTeamDTO объект класса {@link LeftTeamDTO}, содержащий информацию о выходе или исключении игрока из
     *                    команды
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link LeftTeamDTO}, содержащий
     * информацию о том, как игрок покинул команду
     * @throws AppException Если пользователя с таким ником или команды с таким названием не существует в базе данных
     */
    @PostMapping(value = "/leftTeam")
    public ResponseEntity<LeftTeamDTO> leftTeam(@RequestBody LeftTeamDTO leftTeamDTO) throws AppException {
        return ResponseEntity.ok(teamService.leftTeam(leftTeamDTO));
    }
}
