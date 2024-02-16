package com.example.kast.controllers.dto.top;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Класс содержит информацию о топе команд и дате его обновления
 *
 * @author Кирилл "Tamada" Симовин
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FullTopDTO {
    /**
     * Список объектов класса {@link TopGetDTO}, содержащих информацию о командах в топе
     */
    private List<TopGetDTO> topTeams;

    /**
     * Дата обновления топа
     */
    private String topDate;
}
