package com.example.kast.controllers.dto.event;


import lombok.*;


/**
 * Класс описывает информацию, необходимую для изменения заголовка текущего турнира
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EditOngoingEventHeaderDTO {
    /**
     * Название турнира
     */
    private String event;

    /**
     * Призовой фонд турнира
     */
    private String prize;
}
