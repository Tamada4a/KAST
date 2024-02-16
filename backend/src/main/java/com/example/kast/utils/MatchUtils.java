package com.example.kast.utils;

import com.example.kast.controllers.dto.match.map.MapDTO;
import com.example.kast.controllers.dto.matches.MatchTimeByDateDTO;
import com.example.kast.controllers.dto.matches.MatchTimeDTO;
import com.example.kast.controllers.dto.tab.EventMatchDTO;
import com.example.kast.controllers.dto.tab.MatchTabInfoDTO;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.mongo_collections.embedded.Matches;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.kast.utils.Utils.*;


/**
 * Данный класс является утилитой, в которую вынесены часто используемые методы при взаимодействии с матчами
 *
 * @author Кирилл "Tamada" Симовин
 */
public class MatchUtils {
    /**
     * Метод позволяет распределить матчи по соответствующим датам формата <i>Месяц год</i>
     *
     * @param matchesToSort список объектов класса {@link MatchTimeDTO}, содержащих информацию о матчах, которые
     *                      необходимо распределить по датам
     * @param type          тип сортировки матчей:
     *                      <li><b>notreversed</b> - просто отсортированный список</li>
     *                      <li><b>reversed</b> - отсортированный список матчей будет перевернут</li>
     * @return Список объектов класса {@link MatchTimeByDateDTO}, содержащих информацию о матчах, распределенных по
     * соответствующим датам формата <i>Месяц год</i>
     */
    public static ArrayList<MatchTimeByDateDTO> sortMatchesByDateTime(ArrayList<MatchTimeDTO> matchesToSort, String type) {
        ArrayList<MatchTimeByDateDTO> sortedMatches = new ArrayList<>();

        for (MatchTimeDTO match : matchesToSort) {
            String date = match.getDate();
            int idx = getDateIndexFromList(sortedMatches, date, "matches");
            if (idx != -1) {
                ArrayList<MatchTimeDTO> matchesToSet = new ArrayList<>(sortedMatches.get(idx).getMatches());
                matchesToSet.add(match);
                sortedMatches.get(idx).setMatches(matchesToSet);
            } else {
                MatchTimeByDateDTO temp = new MatchTimeByDateDTO(date, List.of(match));
                sortedMatches.add(temp);
            }
        }


        for (MatchTimeByDateDTO day : sortedMatches) {
            ArrayList<MatchTimeDTO> subMatches = new ArrayList<>(day.getMatches());
            subMatches.sort(Comparator.comparingInt(o -> LocalTime.parse(o.getTime()).toSecondOfDay()));
            if (type.equals("reversed"))
                Collections.reverse(subMatches);
            day.setMatches(subMatches);
        }

        sortedMatches.sort(Comparator.comparingInt(o -> parseStringDateToEpochDays(o.getDate())));
        if (type.equals("reversed"))
            Collections.reverse(sortedMatches);

        return sortedMatches;
    }


    /**
     * Метод позволяет получить информацию о текущей карте по ID матча
     *
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      проходит матч
     * @param matchId       ID матча, для которого необходимо получить информацию о текущей карте
     * @return Если матча с запрашиваемым ID не найдено - <code>null</code>.<br></br>
     * Если на данный момент не играется никакой карты - <code>null</code>. <br></br>
     * Иначе объект класса {@link MapDTO}, содержащий информацию о текущей карте
     */
    public static MapDTO getCurrentMapByMatchId(TournamentDoc tournamentDoc, int matchId) {
        Matches match = tournamentDoc.getMatchById(matchId);

        if (match == null)
            return null;

        return match.getCurrentMap();
    }


    /**
     * Метод позволяет отсортировать матчи по дате начала
     *
     * @param matchesToSort список объектов класса {@link EventMatchDTO}, содержащих информацию о матчах, которые
     *                      необходимо отсортировать
     * @param status        статус матчей, которые необходимо получить:
     *                      <li><b>upcoming</b> - ближайшие матчи</li>
     *                      <li><b>ended</b> - завершенные матчи</li>
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      проходят матчи
     * @return Список объектов класса {@link EventMatchDTO}, содержащих информацию о матчах, отсортированных по дате
     * начала
     */
    public static ArrayList<EventMatchDTO> sortMatchesByDate(ArrayList<EventMatchDTO> matchesToSort, String status,
                                                             TournamentDoc tournamentDoc) {
        matchesToSort.sort(Comparator.comparingInt(o ->
                parseStringDateToEpochSeconds(tournamentDoc.getMatchById(o.getMatchId())))
        );
        if (status.equals("ended")) {
            Collections.reverse(matchesToSort);
        }
        return matchesToSort;
    }


    /**
     * Метод позволяет получить информацию о матчах соответствующего турнира, в которых участвует игрок или команда
     *
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      необходимо получить информацию об искомых матчах
     * @param teamName      название команды, для которой необходимо получить список матчей
     * @param player        ник игрока, для которого необходимо получить список матчей
     * @param status        статус матчей, которые необходимо получить:
     *                      <li><b>upcoming</b> - ближайшие матчи</li>
     *                      <li><b>ended</b> - завершенные матчи</li>
     * @return Объект класса {@link MatchTabInfoDTO}, содержащий информацию о матчах соответствующего турнира, в которых
     * участвует игрок или команда
     */
    public static MatchTabInfoDTO getEventWithMatchesByStatus(TournamentDoc tournamentDoc, String teamName, String player,
                                                              String status) {
        MatchTabInfoDTO foundedEvent = new MatchTabInfoDTO();

        foundedEvent.setEvent(tournamentDoc.getName());
        foundedEvent.setPlace(tournamentDoc.getPlace(teamName, player));

        if (foundedEvent.getPlace().isEmpty())
            foundedEvent.setType("upcoming");
        else
            foundedEvent.setType("ended");

        ArrayList<Matches> allEventMatches = tournamentDoc.getMatches();
        ArrayList<EventMatchDTO> eventMatchesByStatusList = new ArrayList<>();

        for (Matches match : allEventMatches) {
            if (match.getStatus().equals(status) && (match.getNameFirst().equals(teamName) || match.getNameSecond().equals(teamName))) {
                EventMatchDTO eventMatch = new EventMatchDTO(
                        match.getMatchId(),
                        parseMatchDate(match.getMatchDate().toLocalDate()), match.getNameFirst(), match.getTagFirst(),
                        match.getNameSecond(), match.getTagSecond(), "-", "-"
                );

                if (!status.equals("upcoming")) {
                    eventMatch.setLeftScore(Integer.toString(match.getMatchTeamScore(match.getNameFirst())));
                    eventMatch.setRightScore(Integer.toString(match.getMatchTeamScore(match.getNameSecond())));
                }

                eventMatchesByStatusList.add(eventMatch);
            }
        }

        if (!eventMatchesByStatusList.isEmpty()) {
            eventMatchesByStatusList.sort(Comparator.comparingInt(o -> parseStringDateToEpochDays(o.getDate())));
        }

        foundedEvent.setMatches(eventMatchesByStatusList);

        return foundedEvent;
    }


    /**
     * Метод позволяет получить все завершенные матчи
     *
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого нужно
     *                      получить завершенные матчи
     * @param player        ник пользователя, чьи завершенные матчи необходимо получить. Может быть пустой строкой, если
     *                      необходимо получить матчи команды или все результаты
     * @param teamName      название команды, чьи завершенные матчи необходимо получить. Может быть пустой строкой, если
     *                      необходимо получить матчи пользователя или все результаты
     * @return Список объектов класса {@link MatchTimeDTO}, содержащих информацию о прошедших матчах, отсортированных
     * по дате начала
     */
    public static ArrayList<MatchTimeDTO> getAttendedEndedMatches(TournamentDoc tournamentDoc, String player, String teamName) {
        ArrayList<MatchTimeDTO> endedMatchesList = new ArrayList<>();

        ArrayList<Matches> allMatchesList = tournamentDoc.getMatches();

        for (Matches match : allMatchesList) {
            if (!match.getStatus().equals("ended") || !isMatchParticipant(player, teamName, match.getNameFirst(), match.getNameSecond()))
                continue;

            MatchTimeDTO evMatch = new MatchTimeDTO(
                    new EventMatchDTO(
                            match.getMatchId(),
                            parseMatchDate(match.getMatchDate().toLocalDate()),
                            match.getNameFirst(),
                            match.getTagFirst(),
                            match.getNameSecond(),
                            match.getTagSecond(),
                            Integer.toString(match.getMatchTeamScore(match.getNameFirst())),
                            Integer.toString(match.getMatchTeamScore(match.getNameSecond()))
                    ),
                    parseMatchTime(match.getMatchDate()),
                    tournamentDoc.getName(),
                    Integer.toString(match.getTier()),
                    match.getMaps());

            endedMatchesList.add(evMatch);
        }

        if (!endedMatchesList.isEmpty()) {
            endedMatchesList.sort(Comparator.comparingInt(o -> parseStringDateToEpochDays(o.getDate())));
        }

        return endedMatchesList;
    }


    /**
     * Метод позволяет конвертировать дату начала матча в строку формата чч:мм
     *
     * @param date дата начала матча
     * @return Строковое представление времени начала матча в формате чч:мм
     */
    public static String parseMatchTime(LocalDateTime date) {
        return fixNumber(date.getHour()) + ":" + fixNumber(date.getMinute());
    }


    /**
     * Метод переводит дату начала матча в количество секунд, прошедших с <i>01.01.1970</i>
     *
     * @param match объект класса {@link Matches}, содержащий информацию о матче, для которого необходимо получить
     *              количество секунд
     * @return Если принимаемый объект <code>null</code> - вернется -1. Иначе количество секунд, прошедших с
     * <i>01.01.1970</i>
     */
    private static int parseStringDateToEpochSeconds(Matches match) {
        if (match == null)
            return -1;

        Instant instant = Instant.now();
        ZoneId systemZone = ZoneId.systemDefault();
        return (int) match.getMatchDate().toEpochSecond(systemZone.getRules().getOffset(instant));
    }


    /**
     * Метод позволяет выяснить, является игрок или команда участником матча
     *
     * @param player     ник пользователя, которого нужно проверить на участие в матче
     * @param teamName   название команды, которую нужно проверить на участие в матче
     * @param firstTeam  название первой команды-участницы матча
     * @param secondTeam название второй команды-участницы матча
     * @return Является ли игрок или команда участником матча: <code>true</code>, если является; <code>false</code> иначе
     */
    private static boolean isMatchParticipant(String player, String teamName, String firstTeam, String secondTeam) {
        if (teamName.isEmpty())
            return player.isEmpty() || player.equals(firstTeam) || player.equals(secondTeam);

        return teamName.equals(firstTeam) || teamName.equals(secondTeam);
    }
}
