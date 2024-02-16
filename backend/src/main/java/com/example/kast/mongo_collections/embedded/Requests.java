package com.example.kast.mongo_collections.embedded;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


/**
 * Класс содержит основную информацию о заявке команды
 *
 * @author Кирилл "Tamada" Симовин, Александр "ugly4" Федякин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Requests {
    /**
     * ID заявки
     */
    private Integer request_id;

    /**
     * Название команды-участницы (может быть как ник игрока (в случае турнира 1x1), так и название команды)
     */
    private String teamName;

    /**
     * Статус команды на турнире:
     * <li><b>accepted</b> - заявка принята. Данный статус возможно установить пока турнир ещё не начался</li>
     * <li><b>await</b> - заявка на рассмотрении. Данный статус возможно установить пока турнир ещё не начался</li>
     * <li><b>kicked</b> - команда исключена с турнира. Данный статус возможно установить только когда турнир
     * начался</li>
     */
    private String status;

    /**
     * Список ников игроков, которые будут представлять на турнире данную команду
     */
    private ArrayList<String> chosenPlayers;
}
