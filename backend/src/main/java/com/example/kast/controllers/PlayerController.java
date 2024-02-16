package com.example.kast.controllers;


import com.example.kast.controllers.dto.player.*;
import com.example.kast.controllers.dto.player.faceit.FaceitAuthDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.services.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

import static com.example.kast.utils.Utils.replaceSpaces;


/**
 * Данный REST-контроллер отвечает за обработку запросов, связанных со страницей пользователя
 *
 * @param playerService объект класса {@link PlayerService} - сервис, обрабатывающий запросы, приходящие со страницы
 *                      пользователя
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record PlayerController(PlayerService playerService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/getFullPlayer/{player}". Используется для получения информации,
     * необходимой для взаимодействия со страницей пользователя
     *
     * @param player ник пользователя, чья страница просматривается
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link FullPlayerDTO},
     * содержащий информацию, необходимую для взаимодействия со страницей игрока на frontend
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    @GetMapping(value = "/getFullPlayer/{player}")
    public ResponseEntity<FullPlayerDTO> getFullPlayer(@PathVariable("player") String player) throws AppException {
        return ResponseEntity.ok(playerService.getFullPlayer(player));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/createTeam". Используется при создании команды
     *
     * @param newTeam объект класса {@link NewTeamDTO}, содержащий информацию о новой команде
     * @return <code>ResponseEntity</code> со статусом 201, тело которого - объект класса {@link NewTeamDTO}, содержащий
     * информацию о созданной команде
     * @throws AppException Если команда с таким названием уже существует в базе данных
     */
    @PostMapping(value = "/createTeam")
    public ResponseEntity<NewTeamDTO> createTeam(@RequestBody NewTeamDTO newTeam) throws AppException {
        NewTeamDTO createdTeam = playerService.createTeam(newTeam);
        return ResponseEntity.created(URI.create("/" + replaceSpaces(createdTeam.getName()))).body(createdTeam);
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/changeBDate". Используется при смене или обновлении даты рождения
     * пользователя
     *
     * @param newDateDTO объект класса {@link NewDateDTO}, содержащий информацию о новой дате рождения пользователя
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link NewDateDTO}, содержащий
     * информацию об измененной дате рождения соответствующего пользователя
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    @PostMapping(value = "/changeBDate")
    public ResponseEntity<NewDateDTO> changeBDate(@RequestBody NewDateDTO newDateDTO) throws AppException {
        return ResponseEntity.ok(playerService.changeBDate(newDateDTO));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/changeNick". Используется при изменении ника пользователя
     *
     * @param newNickDTO объект класса {@link NewNickDTO}, содержащий измененный ник соответствующего игрока
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link NewNickDTO}, содержащий
     * информацию об измененном нике пользователя
     * @throws AppException Если пользователя со старым ником не существует в базе данных или пользователь с новым
     *                      ником уже существует
     */
    @PostMapping(value = "/changeNick")
    public ResponseEntity<NewNickDTO> changeNick(@RequestBody NewNickDTO newNickDTO) throws AppException {
        return ResponseEntity.ok(playerService.changeNick(newNickDTO));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/setVKLink/{player}/{code}". Используется при изменении ссылки на профиль
     * пользователя во ВКонтакте
     *
     * @param player ник игрока, которому необходимо изменить ссылку на профиль во ВКонтакте
     * @param code   временный код, полученный после прохождения авторизации
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - новая ссылка на профиль пользователя во ВКонтакте
     * @throws AppException Если пользователя с таким ником не существует в базе данных; если ссылка на профиль
     *                      ВКонтакте у пользователя уже установлена; если при запросе к API ВКонтакте произошла ошибка;
     *                      если ссылка на привязываемый профиль уже установлена
     * @throws IOException  Если при запросе к API ВКонтакте произошла ошибка
     */
    @PostMapping(value = "/setVKLink/{player}/{code}")
    public ResponseEntity<String> setVKLink(@PathVariable("player") String player,
                                            @PathVariable("code") String code) throws AppException, IOException {
        return ResponseEntity.ok(playerService.setVKLink(player, code));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/setFaceitLink/{player}". Используется при изменении ссылки на профиль
     * пользователя на Faceit
     *
     * @param player        ник игрока, которому необходимо изменить ссылку на профиль Faceit
     * @param faceitAuthDTO объект класса {@link FaceitAuthDTO}, содержащий код авторизации и верификатор, полученные с
     *                      frontend
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - новая ссылка на профиль пользователя на Faceit
     * @throws AppException Если пользователя с таким ником не существует в базе данных; если ссылка на профиль
     *                      Faceit у пользователя уже установлена; если при запросе к API Faceit произошла ошибка;
     *                      если ссылка на привязываемый профиль уже установлена
     * @throws IOException  Если при запросе к API Faceit произошла ошибка
     */
    @PostMapping(value = "/setFaceitLink/{player}")
    public ResponseEntity<String> setFaceitLink(@PathVariable("player") String player,
                                                @RequestBody FaceitAuthDTO faceitAuthDTO) throws AppException, IOException {
        return ResponseEntity.ok(playerService.setFaceitLink(player, faceitAuthDTO));
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getFaceitClientID". Используется для получения client_id приложения на
     * Faceit для PKCE аутентификации
     *
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - client_id приложения на Faceit
     */
    @GetMapping(value = "/getFaceitClientID")
    public ResponseEntity<String> getFaceitClientID() {
        return ResponseEntity.ok(playerService.getFaceitClientID());
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getVKClientID". Используется для получения client_id приложения во
     * ВКонтакте для получения ключа доступа пользователя
     *
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - client_id приложения во ВКонтакте
     */
    @GetMapping(value = "/getVKClientID")
    public ResponseEntity<String> getVKClientID() {
        return ResponseEntity.ok(playerService.getVKClientID());
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getDiscordClientID". Используется для получения client_id приложения в
     * Discord для получения ника пользователя в Discord
     *
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - строка вида <i>STRINGclient_id</i> ввиду того, что
     * client_id Discord в представлении JavaScript не умещается в размер целого числа
     */
    @GetMapping(value = "/getDiscordClientID")
    public ResponseEntity<String> getDiscordClientID() {
        return ResponseEntity.ok(playerService.getDiscordClientID());
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/changeSocial". Используется при установке ссылок на профиль
     * пользователя в Steam и Discord. Также используется для отвязки всех социальных сетей пользователя
     *
     * @param social объект класса {@link ChangeSocialDTO}, содержащий информацию об измененной социальной сети
     *               пользователя
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link ChangeSocialDTO},
     * содержащий информацию об измененной социальной сети пользователя
     * @throws AppException Если пользователя с таким ником не существует в базе данных или привязываемый профиль уже
     *                      установлен у одного из пользователей
     */
    @PostMapping(value = "/changeSocial")
    public ResponseEntity<ChangeSocialDTO> changeSocial(@RequestBody ChangeSocialDTO social) throws AppException {
        return ResponseEntity.ok(playerService.changeSocial(social));
    }
}
