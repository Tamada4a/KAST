package com.example.kast.controllers.dto.matches;


import lombok.Getter;
import lombok.Setter;


/**
 * Класс описывает текущие матчи. Является расширением {@link MatchTimeDTO}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
public class MatchDTO extends MatchTimeDTO {
    /**
     * Счет левой команды по картам
     */
    private Integer leftMapScore;

    /**
     * Счет правой команды по картам
     */
    private Integer rightMapScore;


    /**
     * Инициализирует и создает новый объект класса MatchDTO
     *
     * @param matchTimeDTO  объект класса {@link MatchTimeDTO}, содержащий всю информацию о матче с указанием времени,
     *                      необходимую для отображения
     * @param leftMapScore  счет левой команды по картам
     * @param rightMapScore счет правой команды по картам
     */
    public MatchDTO(MatchTimeDTO matchTimeDTO, Integer leftMapScore, Integer rightMapScore) {
        this.setMatchId(matchTimeDTO.getMatchId());
        this.setDate(matchTimeDTO.getDate());
        this.setLeftTeam(matchTimeDTO.getLeftTeam());
        this.setLeftTag(matchTimeDTO.getLeftTag());
        this.setRightTeam(matchTimeDTO.getRightTeam());
        this.setRightTag(matchTimeDTO.getRightTag());
        this.setLeftScore(matchTimeDTO.getLeftScore());
        this.setRightScore(matchTimeDTO.getRightScore());
        this.setTime(matchTimeDTO.getTime());
        this.setEvent(matchTimeDTO.getEvent());
        this.setTier(matchTimeDTO.getTier());
        this.setMaps(matchTimeDTO.getMaps());
        this.leftMapScore = leftMapScore;
        this.rightMapScore = rightMapScore;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link MatchDTO}
     *
     * @return Строковое представление объекта класса {@link MatchDTO}
     */
    @Override
    public String toString() {
        return String.format("MatchDTO(matchId=%d, date=%s, leftTeam=%s, leftTag=%s, rightTeam=%s, rightTag=%s, " +
                        "leftScore=%s, rightScore=%S, time=%s, event=%s, tier=%s, maps=%s, leftMapScore=%d, rightMapScore=%d)",
                this.getMatchId(), this.getDate(), this.getLeftTeam(), this.getLeftTag(), this.getRightTeam(),
                this.getRightTag(), this.getLeftScore(), this.getRightScore(), this.getTime(), this.getEvent(),
                this.getTier(), this.getMaps().toString(), leftMapScore, rightMapScore);
    }
}
