package com.example.kast.controllers.dto.event;


import com.example.kast.mongo_collections.embedded.Requests;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;


/**
 * Класс является расширением {@link Requests}.
 * Используется для грамотного отображения команд-участниц на странице турнира
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
@NoArgsConstructor
public class ParticipantDTO extends Requests {
    /**
     * Тип заявки: team (команда) или player (игрок)
     */
    private String type;

    /**
     * Тэг команды игрока (для турниров 1x1)
     */
    private String tag;

    /**
     * Название команды игрока (для турниров 1x1)
     */
    private String team;


    /**
     * Инициализирует и создает новый объект класса ParticipantDTO
     *
     * @param request_id    ID заявки
     * @param teamName      название команды-участницы (может быть как ник игрока (в случае турнира 1x1), так и
     *                      название команды)
     * @param status        статус команды на турнире:
     *                      <li><b>accepted</b> - заявка принята. Данный статус возможно установить пока турнир ещё не
     *                      начался</li>
     *                      <li><b>await</b> - заявка на рассмотрении. Данный статус возможно установить пока турнир ещё
     *                      не начался</li>
     *                      <li><b>kicked</b> - команда исключена с турнира. Данный статус возможно установить только
     *                      когда турнир начался</li>
     *                      <li><b>Пустая строка</b> - используется в случае, если статус заявки <i>accepted</i> и
     *                      турнир либо начался, либо завершен</li>
     * @param chosenPlayers список ников игроков, которые будут участвовать в турнире
     * @param type          тип заявки: team (команда) или player (игрок)
     * @param tag           тэг команды игрока (для турниров 1x1)
     * @param team          название команды игрока (для турниров 1x1)
     */
    public ParticipantDTO(Integer request_id, String teamName, String status, ArrayList<String> chosenPlayers,
                          String type, String tag, String team) {
        this.setRequest_id(request_id);
        this.setTeamName(teamName);
        this.setStatus(status);
        this.setChosenPlayers(chosenPlayers);
        this.type = type;
        this.tag = tag;
        this.team = team;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link ParticipantDTO}
     *
     * @return Строковое представление объекта класса {@link ParticipantDTO}
     */
    @Override
    public String toString() {
        return String.format("ParticipantDTO(request_id=%d, teamName=%s, status=%s, chosenPlayers=%s, type=%s," +
                        "tag=%s, team=%s)", this.getRequest_id(), this.getTeamName(), this.getStatus(),
                this.getChosenPlayers(), type, tag, team);
    }
}
