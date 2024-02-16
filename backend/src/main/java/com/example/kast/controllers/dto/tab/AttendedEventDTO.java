package com.example.kast.controllers.dto.tab;


import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * Класс содержит информацию о посещенных турнирах игроком или командой. Является расширением {@link EventInfoDTO}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@AllArgsConstructor
public class AttendedEventDTO extends EventInfoDTO {
    /**
     * Название команды
     */
    private String team;

    /**
     * Ник игрока, ставшего MVP турнира
     */
    private String mvp;


    /**
     * Инициализирует и создает новый объект класса AttendedEventDTO
     *
     * @param eventName название турнира
     * @param date      дата турнира в формате дд.мм.гггг - дд.мм.гггг
     * @param place     занятое место на турнире
     * @param team      название команды
     * @param mvp       ник игрока, ставшего MVP турнира
     */
    public AttendedEventDTO(String eventName, String date, String place, String team, String mvp) {
        this.setName(eventName);
        this.setDate(date);
        this.setPlace(place);
        this.team = team;
        this.mvp = mvp;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link AttendedEventDTO}
     *
     * @return Строковое представление объекта класса {@link AttendedEventDTO}
     */
    @Override
    public String toString() {
        return String.format("AttendedEventDTO(name=%s, date=%s, place=%s, team=%s, mvp=%s)",
                this.getName(), this.getDate(), this.getPlace(), team, mvp);
    }
}
