package com.example.kast.controllers.dto.top;


import com.example.kast.controllers.dto.other.FlagNameSrcDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


/**
 * Класс описывает информацию о команде, находящейся в топе
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopGetDTO {
    /**
     * Название команды
     */
    private String name;

    /**
     * Позиция команды в топе
     */
    private Integer topPosition;

    /**
     * На сколько позиций изменилось положение команды в топе после обновления топа
     */
    private Integer changedPosition;

    /**
     * Список объектов класса {@link FlagNameSrcDTO}, содержащих информацию об игроках команды
     */
    private ArrayList<FlagNameSrcDTO> players;
}
