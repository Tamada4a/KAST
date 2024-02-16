package com.example.kast.controllers.dto.player.faceit;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит код авторизации и верификатор, полученные с frontend
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FaceitAuthDTO {
    /**
     * Одноразовый код авторизации, полученный с frontend
     */
    private String code;

    /**
     * Верификатор, сгенерированный на frontend
     */
    private String verifier;
}
