package com.example.kast.controllers.dto.other;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Класс содержит данные, необходимые для демонстрации уведомления
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
@AllArgsConstructor
public class NotificationsDTO {
    /**
     * Текст уведомления
     */
    private String description;

    /**
     * Тип уведомления - определяет цвет рамки:
     * <li><b>warn</b> - ошибка, красный цвет</li>
     * <li><b>ok</b> - все хорошо, зеленый цвет</li>
     * <li><b>neutral</b> - нейтральное уведомление, белый цвет</li>
     */
    private String type;
}
