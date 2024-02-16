package com.example.kast.config;


import com.example.kast.dto.ErrorDTO;
import com.example.kast.exceptions.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Данный контроллер срабатывает при появлении ошибки {@link AppException}. Ошибка заворачивается в
 * ResponseEntity и возвращается пользователю с указанием сообщения и кодом ошибки
 *
 * @author Кирилл "Tamada" Симовин
 */
@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(value = {AppException.class})
    @ResponseBody
    public ResponseEntity<ErrorDTO> handlerException(AppException exception) {
        return ResponseEntity.status(exception.getCode())
                .body(ErrorDTO.builder().message(exception.getMessage()).build());
    }
}
