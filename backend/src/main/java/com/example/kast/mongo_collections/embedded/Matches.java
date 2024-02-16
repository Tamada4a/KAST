package com.example.kast.mongo_collections.embedded;


import com.example.kast.controllers.dto.match.PicksDTO;
import com.example.kast.controllers.dto.match.StreamDTO;
import com.example.kast.controllers.dto.match.map.MapDTO;
import com.example.kast.controllers.dto.match.map.MapStatsDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static com.example.kast.utils.Utils.fixNumber;


/**
 * Класс содержит информацию о матче. Используется для сохранения информации о матче в базу данных
 *
 * @author Кирилл "Tamada" Симовин, Александр "ugly4" Федякин
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Matches {
    /**
     * ID матча
     */
    private Integer matchId;

    /**
     * Дата начала матча
     */
    private LocalDateTime matchDate;

    /**
     * Дата окончания матча. Используется для определения статуса матч:
     * <li><b>ended</b> - матч закончен</li>
     * <li><b>playing</b> - матч играется</li>
     * <li><b>upcoming</b> - матч пока не начался</li>
     */
    private LocalDateTime matchEndDate;

    /**
     * Название первой команды
     */
    private String nameFirst;

    /**
     * Тэг первой команды
     */
    private String tagFirst;

    /**
     * Тэг второй команды
     */
    private String tagSecond;

    /**
     * Название второй команды
     */
    private String nameSecond;

    /**
     * Значимость матча (от 1 до 5 звезд)
     */
    private Integer tier;

    /**
     * Список объектов класса {@link MapDTO} - карт матча
     */
    private ArrayList<MapDTO> maps;

    /**
     * Описание матча
     */
    private String description;

    /**
     * Список объектов класса {@link StreamDTO} - стримов, транслирующих матч
     */
    private ArrayList<StreamDTO> streams;

    /**
     * Список объектов класса {@link PicksDTO} - действий команды при пике/бане карт. Например:
     * <i>команда ПУПА банит карту Nuke</i>
     */
    private ArrayList<PicksDTO> picks;

    /**
     * IP-адрес сервера, на котором играется матч. Виден только игрокам, участвующим в матче
     */
    private String ip;


    /**
     * Переопределенный метод строкового представления объекта класса {@link Matches}
     *
     * @return Строковое представление объекта класса {@link Matches}
     */
    @Override
    public String toString() {
        String day = fixNumber(matchDate.getDayOfMonth());
        String month = fixNumber(matchDate.getMonthValue());
        String hour = fixNumber(matchDate.getHour());
        String minutes = fixNumber(matchDate.getMinute());

        String dayEnd = fixNumber(matchEndDate.getDayOfMonth());
        String monthEnd = fixNumber(matchEndDate.getMonthValue());
        String hourEnd = fixNumber(matchEndDate.getHour());
        String minutesEnd = fixNumber(matchEndDate.getMinute());

        return String.format("Matches(matchId=%s, matchDate=%s-%s-%d %s:%s, matchEndDate=%s-%s-%d %s:%s, nameFirst=%s, " +
                        "tagFirst=%s, tagSecond=%s, nameSecond=%s, tier=%s, maps=%s, description=%s, " +
                        "streams=%s, picks=%s, ip=%s)",
                matchId, day, month, matchDate.getYear(), hour, minutes,
                dayEnd, monthEnd, matchEndDate.getYear(), hourEnd, minutesEnd,
                nameFirst, tagFirst, tagSecond, nameSecond, tier, maps.toString(), description,
                streams.toString(), picks.toString(), ip);
    }


    /**
     * Метод позволяет определить выиграла ли матч одна из команд
     *
     * @return Является ли матч завершенным: <code>true</code>, если да; <code>false</code> иначе
     */
    public boolean isSomebodyWon() {
        ArrayList<MapDTO> maps = this.getMaps();

        ArrayList<Integer> score = getScoreByMap();

        int sum = score.get(0) + score.get(1);

        if (sum == maps.size())
            return true;

        double halfMap = Math.ceil((double) maps.size() / 2);

        return (halfMap == score.get(0) || halfMap == score.get(1));
    }


    /**
     * Метод позволяет получить текущий счет по картам среди команд
     *
     * @return Список из двух целых чисел - счет по картам первой и второй команд
     */
    public ArrayList<Integer> getScoreByMap() {
        ArrayList<Integer> scoreMap = new ArrayList<>();
        scoreMap.add(0);
        scoreMap.add(0);

        maps.forEach((map) -> {
            if (map.getStatus().equals("ended") && map.getStats() != null && map.getStats().getSecondTeam() != null && map.getStats().getSecondTeam().getScore() != null && map.getStats().getFirstTeam() != null && map.getStats().getFirstTeam().getScore() != null) {

                if (map.getStats().getFirstTeam().getName().equals(nameFirst)) {
                    if (map.getStats().getFirstTeam().getScore() > map.getStats().getSecondTeam().getScore()) {
                        scoreMap.set(0, scoreMap.get(0) + 1);
                    } else if (map.getStats().getFirstTeam().getScore() < map.getStats().getSecondTeam().getScore()) {
                        scoreMap.set(1, scoreMap.get(1) + 1);
                    }
                } else {
                    if (map.getStats().getSecondTeam().getScore() > map.getStats().getFirstTeam().getScore()) {
                        scoreMap.set(0, scoreMap.get(0) + 1);
                    } else if (map.getStats().getSecondTeam().getScore() < map.getStats().getFirstTeam().getScore()) {
                        scoreMap.set(1, scoreMap.get(1) + 1);
                    }
                }
            }
        });
        return scoreMap;
    }


    /**
     * Метод позволяет получить счет команд на текущей карте или последней законченной
     *
     * @return Список из двух целых чисел - счет каждой из команд на текущей карте или последней законченной
     */
    public ArrayList<Integer> getCurrentScoreOnMap() {
        ArrayList<Integer> scoreMap = new ArrayList<>();

        for (int i = 0; i < maps.size(); ++i) {
            MapDTO map = maps.get(i);
            if (map.getStatus().equals("playing") ||
                    (map.getStatus().equals("ended") && getStatus().equals("playing")
                            && (i == (maps.size() - 1) || maps.get(i + 1).getStatus().equals("upcoming")))) {

                if (map.getStats().getFirstTeam().getName().equals(nameFirst)) {
                    scoreMap.add(map.getStats().getFirstTeam().getScore());
                    scoreMap.add(map.getStats().getSecondTeam().getScore());
                } else {
                    scoreMap.add(map.getStats().getSecondTeam().getScore());
                    scoreMap.add(map.getStats().getFirstTeam().getScore());
                }
            }
        }

        return scoreMap;
    }


    /**
     * Метод позволяет получить текущую карту матча
     *
     * @return Если найдена текущая карта - объект класса {@link MapDTO}, содержащий информацию о текущей карте, иначе
     * <i>null</i>
     */
    public MapDTO getCurrentMap() {
        for (int i = 0; i < maps.size(); ++i) {
            MapDTO map = maps.get(i);
            if (map.getStatus().equals("playing") ||
                    (map.getStatus().equals("ended") && getStatus().equals("playing")
                            && (i == (maps.size() - 1) || maps.get(i + 1).getStatus().equals("upcoming")))) {
                return map;
            }
        }
        return null;
    }


    /**
     * Метод позволяет определить статус матча с использованием даты окончания матча:
     * <li><b>ended</b> - матч закончен</li>
     * <li><b>playing</b> - матч играется</li>
     * <li><b>upcoming</b> - матч пока не начался</li>
     *
     * @return Статус матча
     */
    public String getStatus() {
        long minutes = -99090;
        if (matchEndDate != null)
            minutes = ChronoUnit.MINUTES.between(matchEndDate, LocalDateTime.now());

        if (isSomebodyWon() && (minutes > 3 || minutes == -99090))
            return "ended";

        if (!LocalDateTime.now().isBefore(matchDate))
            return "playing";

        return "upcoming";
    }


    /**
     * Метод переводит статус матча в целочисленный эквивалент:
     * <li><b>0</b> - upcoming - матч пока не начался</li>
     * <li><b>1</b> - playing - матч играется</li>
     * <li><b>2</b> - ended - матч закончен</li>
     *
     * @return Статус матча в целочисленном виде
     */
    public Integer getMatchStatus() {
        String status = getStatus();

        if (status.equals("upcoming"))
            return 0;

        if (status.equals("playing"))
            return 1;

        return 2;
    }


    /**
     * Метод позволяет определить счет команды в матче
     *
     * @param teamName название команды, чей счет необходимо получить
     * @return Счет команды в матче. Если матч не best of 1, возвращается счет по картам.
     * В ином случае возвращается счет на карте
     */
    public Integer getMatchTeamScore(String teamName) {
        if (!getStatus().equals("ended"))
            return 0;

        if (maps.size() == 1) {
            MapStatsDTO statsDTO = maps.get(0).getStats();
            if (teamName.equals(statsDTO.getFirstTeam().getName()))
                return statsDTO.getFirstTeam().getScore();
            return statsDTO.getSecondTeam().getScore();
        }

        ArrayList<Integer> scores = getScoreByMap();
        if (teamName.equals(nameFirst))
            return scores.get(0);
        return scores.get(1);
    }
}
