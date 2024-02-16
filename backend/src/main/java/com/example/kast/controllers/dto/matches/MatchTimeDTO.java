package com.example.kast.controllers.dto.matches;


import com.example.kast.controllers.dto.match.map.MapDTO;
import com.example.kast.controllers.dto.tab.EventMatchDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;


/**
 * Класс описывает матч с указанием времени начала. Является расширением {@link EventMatchDTO}
 *
 * @author Кирилл "Tamada" Симовин
 */
@NoArgsConstructor
@Setter
@Getter
public class MatchTimeDTO extends EventMatchDTO {
    /**
     * Время начала матча в формате чч:мм
     */
    private String time;

    /**
     * Название турнира
     */
    private String event;

    /**
     * Значимость матча (от 1 до 5 звезд)
     */
    private String tier;

    /**
     * Список объектов класса {@link MapDTO} - карт матча
     */
    private ArrayList<MapDTO> maps;


    /**
     * Инициализирует и создает новый объект класса MatchTimeDTO
     *
     * @param eventMatchDTO объект класса {@link EventMatchDTO}, содержащий основную информацию о матче, необходимую для
     *                      отображения во вкладке матчей на странице игрока или команды
     * @param time          время начала матча в формате чч:мм
     * @param event         название турнира
     * @param tier          значимость матча (от 1 до 5 звезд)
     * @param maps          список объектов класса {@link MapDTO} - карт матча
     */
    public MatchTimeDTO(EventMatchDTO eventMatchDTO, String time, String event, String tier, ArrayList<MapDTO> maps) {
        this.setMatchId(eventMatchDTO.getMatchId());
        this.setDate(eventMatchDTO.getDate());
        this.setLeftTeam(eventMatchDTO.getLeftTeam());
        this.setLeftTag(eventMatchDTO.getLeftTag());
        this.setRightTeam(eventMatchDTO.getRightTeam());
        this.setRightTag(eventMatchDTO.getRightTag());
        this.setLeftScore(eventMatchDTO.getLeftScore());
        this.setRightScore(eventMatchDTO.getRightScore());
        this.time = time;
        this.event = event;
        this.tier = tier;
        this.maps = maps;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link MatchTimeDTO}
     *
     * @return Строковое представление объекта класса {@link MatchTimeDTO}
     */
    @Override
    public String toString() {
        return String.format("MatchTimeDTO(matchId=%d, date=%s, leftTeam=%s, leftTag=%s, rightTeam=%s, rightTag=%s, " +
                        "leftScore=%s, rightScore=%S, time=%s, event=%s, tier=%s, maps=%s)", this.getMatchId(),
                this.getDate(), this.getLeftTeam(), this.getLeftTag(), this.getRightTeam(), this.getRightTag(),
                this.getLeftScore(), this.getRightScore(), time, event, tier, maps.toString());
    }
}
