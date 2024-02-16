package com.example.kast.controllers;


import com.example.kast.controllers.dto.event.ChosenEventDTO;
import com.example.kast.controllers.dto.event.EditOngoingEventHeaderDTO;
import com.example.kast.controllers.dto.event.FullEventDTO;
import com.example.kast.controllers.dto.event.ParticipantDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.embedded.PrizePlaces;
import com.example.kast.mongo_collections.embedded.Requests;
import com.example.kast.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


/**
 * Данный REST-контроллер отвечает за обработку запросов, связанных со страницей турнира
 *
 * @param eventService объект класса {@link EventService} - сервис, обрабатывающий запросы, приходящие со страницы
 *                     турнира
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record EventController(EventService eventService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/getFullEvent/{event}/{player}" для получения всей информации
     * для взаимодействия со страницей турнира
     *
     * @param event  название турнира, для которого получается информация
     * @param player ник игрока, который просматривает страницу
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link FullEventDTO},
     * содержащий всю информацию, необходимую для взаимодействия со страницей турнира
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    @GetMapping(value = "/getFullEvent/{event}/{player}")
    public ResponseEntity<FullEventDTO> getFullEvent(@PathVariable("event") String event,
                                                     @PathVariable("player") String player) throws AppException {
        return ResponseEntity.ok(eventService.getFullEvent(event, player));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editUpcomingEvent/{oldEvent}". Используется при редактировании еще не
     * начавшегося турнира
     *
     * @param chosenEventDTO объект класса {@link ChosenEventDTO}, содержащий измененную информацию о турнире
     * @param oldEvent       старое название турнира (может совпадать с названием после изменения). Используется для
     *                       определения турнира для изменения
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link ChosenEventDTO},
     * содержащий информацию об измененном турнире
     * @throws AppException Если турнира с таким названием не существует в базе данных или новое название турнира уже
     *                      занято
     */
    @PostMapping(value = "/editUpcomingEvent/{oldEvent}")
    public ResponseEntity<ChosenEventDTO> editUpcomingEvent(@PathVariable("oldEvent") String oldEvent,
                                                            @RequestBody ChosenEventDTO chosenEventDTO) throws AppException {
        return ResponseEntity.ok(eventService.editUpcomingEvent(chosenEventDTO, oldEvent));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editOngoingEventHeader/{oldEvent}". Используется при редактировании
     * заголовка текущего турнира
     *
     * @param oldEvent                  старое название турнира (может совпадать с названием после изменения). Используется
     *                                  для определения турнира для изменения
     * @param editOngoingEventHeaderDTO объект класса {@link EditOngoingEventHeaderDTO}, содержащий измененную информацию
     *                                  о заголовке турнира
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link EditOngoingEventHeaderDTO},
     * содержащий информацию об измененном заголовке турнира
     * @throws AppException Если турнира с таким названием не существует в базе данных или новое название турнира уже
     *                      занято
     */
    @PostMapping(value = "/editOngoingEventHeader/{oldEvent}")
    public ResponseEntity<EditOngoingEventHeaderDTO> editOngoingEvent(@PathVariable("oldEvent") String oldEvent,
                                                                      @RequestBody EditOngoingEventHeaderDTO editOngoingEventHeaderDTO) throws AppException {
        return ResponseEntity.ok(eventService.editOngoingEventHeader(editOngoingEventHeaderDTO, oldEvent));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/addNewRequest/{event}". Используется при регистрации команды на турнир
     *
     * @param event          название турнира, на который регистрируется команда
     * @param participantDTO объект класса {@link ParticipantDTO}, содержащий информацию о зарегистрированной команде
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link Requests}, содержащий
     * основную информацию о заявке команды
     * @throws AppException Если турнира с таким названием не существует в базе данных или данная команда уэе отправила
     *                      заявку
     */
    @PostMapping(value = "/addNewRequest/{event}")
    public ResponseEntity<Requests> addNewRequest(@PathVariable("event") String event,
                                                  @RequestBody ParticipantDTO participantDTO) throws AppException {
        return ResponseEntity.ok(eventService.addNewRequest(event, participantDTO));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editChosenPlayers/{event}/{teamName}". Используется при редактировании
     * состава команды на турнир
     *
     * @param event         название турнира, в рамках которого меняется список участников
     * @param teamName      название команды, изменяющей список участников
     * @param chosenPlayers список ников игроков, которые будут участвовать в турнире
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - обновленный список ников игроков,
     * принимающих участие в турнире
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    @PostMapping(value = "/editChosenPlayers/{event}/{teamName}")
    public ResponseEntity<ArrayList<String>> editChosenPlayers(@PathVariable("event") String event,
                                                               @PathVariable("teamName") String teamName,
                                                               @RequestBody ArrayList<String> chosenPlayers) throws AppException {
        return ResponseEntity.ok(eventService.editChosenPlayers(event, teamName, chosenPlayers));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editDiskUrl/{event}". Используется при редактировании ссылки на
     * диск с фотографиями с турнира
     *
     * @param event   название турнира, у которого изменяется ссылка
     * @param diskUrl новая ссылка на диск
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - новая ссылка на диск
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    @PostMapping(value = "/editDiskUrl/{event}")
    public ResponseEntity<String> editDiskUrl(@PathVariable("event") String event, @RequestBody String diskUrl) throws AppException {
        return ResponseEntity.ok(eventService.editDiskUrl(event, diskUrl));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editPrizePlaces/{event}". Используется при редактировании призовых
     * мест турнира
     *
     * @param event       название турнира, у которого редактируются призовые места
     * @param prizePlaces список объектов класса {@link PrizePlaces}, содержащих информацию об измененном распределении
     *                    призовых мест на турнире
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - список объектов класса {@link PrizePlaces},
     * содержащих информацию об измененном распределении призовых мест на турнире
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    @PostMapping(value = "/editPrizePlaces/{event}")
    public ResponseEntity<ArrayList<PrizePlaces>> editPrizePlaces(@PathVariable("event") String event,
                                                                  @RequestBody ArrayList<PrizePlaces> prizePlaces) throws AppException {
        return ResponseEntity.ok(eventService.editPrizePlaces(event, prizePlaces));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editEventDescription/{event}". Используется при изменении информации
     * о проведении турнира - описания
     *
     * @param event       название турнира, у которого изменяется описание
     * @param description новое описание турнира
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - измененное описание турнира
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    @PostMapping(value = "/editEventDescription/{event}")
    public ResponseEntity<String> editEventDescription(@PathVariable("event") String event,
                                                       @RequestBody String description) throws AppException {
        return ResponseEntity.ok(eventService.editEventDescription(event, description));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editEventMapPool/{event}". Используется при изменении маппула турнира
     *
     * @param event   название турнира, у которого изменяется маппул
     * @param mapPool измененный список названий карт, доступных в рамках турнира - измененный маппул
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - измененный список названий карт, доступных
     * в рамках турнира
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    @PostMapping(value = "/editEventMapPool/{event}")
    public ResponseEntity<ArrayList<String>> editEventMapPool(@PathVariable("event") String event,
                                                              @RequestBody ArrayList<String> mapPool) throws AppException {
        return ResponseEntity.ok(eventService.editEventMapPool(event, mapPool));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editRequestStatus/{event}/{teamName}/{status}". Используется при
     * изменении статуса заявки команды
     *
     * @param event    название турнира, в рамках которого меняется статус команды
     * @param status   новый статус команды на турнире:
     *                 <li><b>kicked</b> - команда исключена. Статус команды может принять данное значение только если
     *                 турнир уже начался</li>
     *                 <li><b>accepted</b> - заявка принята</li>
     *                 <li><b>await</b> - заявка на рассмотрении</li>
     * @param teamName название команды, у которой изменяется статус
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link Requests}, содержащий
     * информацию об измененной заявке команды
     * @throws AppException Если турнира с таким названием не существует в базе данных, или статус команды, или указана
     *                      незарегистрированная на турнире команда
     */
    @PostMapping(value = "/editRequestStatus/{event}/{teamName}/{status}")
    public ResponseEntity<Requests> editRequestStatus(@PathVariable("event") String event,
                                                      @PathVariable("teamName") String teamName,
                                                      @PathVariable("status") String status) throws AppException {
        return ResponseEntity.ok(eventService.editRequestStatus(event, teamName, status));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/deleteRequest/{event}/{teamName}". Используется при отклонении
     * заявки команды у ещё не начавшегося турнира
     *
     * @param event    название турнира, в рамках которого отклоняется заявка команды
     * @param teamName название команды, заявка которой отклоняется
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link Requests}, содержащий
     * информацию об удаленной заявке команды
     * @throws AppException Если турнира с таким названием не существует в базе данных или указана незарегистрированная
     *                      на турнире команда
     */
    @PostMapping(value = "/deleteRequest/{event}/{teamName}")
    public ResponseEntity<Requests> deleteRequest(@PathVariable("event") String event,
                                                  @PathVariable("teamName") String teamName) throws AppException {
        return ResponseEntity.ok(eventService.deleteRequest(event, teamName));
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/editEventMVP/{event}/{player}". Используется при указании самого
     * ценного игрока турнира - MVP
     *
     * @param event  название турнира, в рамках которого игрок стал MVP
     * @param player ник игрока, ставшего MVP в рамках данного турнира
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - ник игрока, ставшего MVP турнира
     * @throws AppException Если в базе данных не существует турнира с таким названием или пользователя с таким ником
     */
    @PostMapping(value = "/editEventMVP/{event}/{player}")
    public ResponseEntity<String> editEventMVP(@PathVariable("event") String event,
                                               @PathVariable("player") String player) throws AppException {
        return ResponseEntity.ok(eventService.editEventMVP(event, player));
    }
}
