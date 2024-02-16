package com.example.kast.controllers.dto.match;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс описывает иконку законченного раунда
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@AllArgsConstructor
public class RoundHistoryDTO {
    /**
     * Победитель в раунде: CT или T
     */
    private String winner;

    /**
     * Как закончился раунд:
     * <li><b>Skull</b> - команда уничтожена</li>
     * <li><b>BombExploded</b> - взорвана бомба</li>
     * <li><b>Timer</b> - закончилось время раунда</li>
     * <li><b>BombDefused</b> - бомба обезврежена</li>
     */
    private String how;
}
