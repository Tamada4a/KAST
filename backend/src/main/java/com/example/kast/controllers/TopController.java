package com.example.kast.controllers;


import com.example.kast.controllers.dto.top.FullTopDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.services.TopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Данный REST-контроллер отвечает за обработку запросов, связанных со страницей топа
 *
 * @param topService объект класса {@link TopService} - сервис, обрабатывающий запросы, приходящие со страницы топа
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record TopController(TopService topService) {
    /**
     * Метод обрабатывает GET-запрос по пути "/getFullTop". Используется при получении информации о топе команд и дате
     * его обновления
     *
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link FullTopDTO}, содержащий
     * информацию о топе команд и дате его обновления
     */
    @GetMapping(value = "/getFullTop")
    public ResponseEntity<FullTopDTO> getFullTop() {
        return ResponseEntity.ok(topService.getFullTop());
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/setTop". Используется для изменения положения команд в топе
     *
     * @param topSet объект класса {@link FullTopDTO}, содержащий информацию об измененном положении команд в топе
     * @return <code>ResponseEntity</code> со статусом 200, тело которого - объект класса {@link FullTopDTO}, содержащий
     * информацию об измененном топе команд и дате его обновления
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    @PostMapping(value = "/setTop")
    public ResponseEntity<FullTopDTO> setTop(@RequestBody FullTopDTO topSet) throws AppException {
        return ResponseEntity.ok(topService.setTop(topSet));
    }
}
