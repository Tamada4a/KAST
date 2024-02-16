package com.example.kast.exceptions;


import org.springframework.http.HttpStatus;


/**
 * Данный класс обрабатывает ошибки, полученные при запросах с указанием HTTP-кода. Является расширением
 * {@link RuntimeException}
 *
 * @author Кирилл "Tamada" Симовин
 */
public class AppException extends RuntimeException {
    /**
     * HTTP-код, с которым выбрасывается ошибка
     */
    private final HttpStatus code;


    /**
     * Конструктор исключения
     *
     * @param code    HTTP-код ошибки
     * @param message сообщение ошибки
     */
    public AppException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }


    /**
     * Возвращает HTTP-код ошибки
     */
    public HttpStatus getCode() {
        return code;
    }
}
