package com.example.kast.controllers.dto.match;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


/**
 * Класс используется при редактировании IP-адреса сервера, на котором проходит матч, и списка стримов,
 * транслирующих матч
 *
 * @author Кирилл "Tamada" Симовин
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EditMatchStreamsDTO {
    /**
     * Список объектов класса {@link StreamDTO} - стримов, транслирующих матч
     */
    private ArrayList<StreamDTO> streams;

    /**
     * IP-адрес сервера, на котором проходит матч
     */
    private String ip;
}
