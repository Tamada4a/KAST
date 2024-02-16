package com.example.kast.controllers.dto.match;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Класс описывает информацию о стриме, которую пользователь видит на странице матча. Является расширением {@link StreamDTO}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
@NoArgsConstructor
public class StreamFlagDTO extends StreamDTO {
    /**
     * Путь до флага трансляции на frontend
     */
    private String flagPath;

    /**
     * Количество зрителей трансляции. Может быть пустым, если трансляция закончилась
     */
    private String viewers;


    /**
     * Инициализирует и создает новый объект класса StreamFlagDTO
     *
     * @param stream   объект класса {@link StreamDTO}, содержащий основную информацию о стриме:
     *                 страну стримера/язык трансляции, ник стримера, ссылку на трансляцию
     * @param flagPath путь до флага трансляции на frontend
     * @param viewers  количество зрителей трансляции. Может быть пустым, если трансляция закончилась
     */
    public StreamFlagDTO(StreamDTO stream, String flagPath, String viewers) {
        this.setCountry(stream.getCountry());
        this.setLink(stream.getLink());
        this.setName(stream.getName());
        this.flagPath = flagPath;
        this.viewers = viewers;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link StreamFlagDTO}
     *
     * @return Строковое представление объекта класса {@link StreamFlagDTO}
     */
    @Override
    public String toString() {
        return String.format("StreamFlagDTO(country=%s, link=%s, name=%s, flagPath=%s, viewers=%s)", this.getCountry(),
                this.getLink(), this.getName(), flagPath, viewers);
    }
}
