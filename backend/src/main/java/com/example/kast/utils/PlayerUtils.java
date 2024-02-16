package com.example.kast.utils;


import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.embedded.Rosters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Данный класс является утилитой, в которую вынесены часто используемые методы при взаимодействии с игроками
 *
 * @author Кирилл "Tamada" Симовин
 */
public class PlayerUtils {
    /**
     * Метод позволяет получить текущую команду игрока
     *
     * @param playerDoc объект класса {@link PlayerDoc}, содержащий информацию об игроке, чью команду необходимо получить
     * @return Если пользователь не состоит ни в одной команде - пустая строка, иначе - название текущей команды
     * пользователя
     */
    public static String getTeam(PlayerDoc playerDoc) {
        AtomicReference<String> teamName = new AtomicReference<>("");

        ArrayList<Rosters> rosters = playerDoc.getRosters();

        for (Rosters roster : rosters) {
            if (roster.getExitDate() == null) {
                teamName.set(roster.getTeamName());
                break;
            }
        }

        return teamName.get();
    }


    /**
     * Метод позволяет определить, входил ли игрок в состав команды в указанный период
     *
     * @param eventStart дата начала турнира
     * @param eventEnd   дата окончания турнира
     * @param teamEnter  дата вхождения игрока в команду
     * @param teamExit   дата выхода игрока из команды. Может быть <code>null</code>, если на текущей момент игрок
     *                   состоит в данной команде
     * @return Входил ли игрок в состав команды в указанный период: <code>true</code>, если да; <code>false</code> иначе
     */
    public static boolean isInTeam(LocalDate eventStart, LocalDate eventEnd, LocalDate teamEnter, LocalDate teamExit) {
        if (teamExit == null)
            teamExit = LocalDate.MAX;

        if (!eventStart.isAfter(teamEnter) && isInInterval(eventEnd, teamEnter, teamExit)) {
            return true;
        }

        if (!eventStart.isBefore(teamEnter) && isInInterval(teamExit, eventStart, eventEnd)) {
            return true;
        }

        if (eventStart.equals(teamEnter) && eventEnd.equals(teamExit)) {
            return true;
        }

        if (isInInterval(eventStart, teamEnter, teamExit) && isInInterval(eventEnd, teamEnter, teamExit)) {
            return true;
        }

        return isInInterval(teamEnter, eventStart, eventEnd) && isInInterval(teamExit, eventStart, eventEnd);
    }


    /**
     * Метод позволяет определить, входит ли дата в указанный интервал
     *
     * @param value значение, для которого требуется определить вхождение в интервал
     * @param start начало интервала
     * @param end   конец интервала
     * @return Входит ли значение в заданный интервал: <code>true</code>, если да; <code>false</code> иначе
     */
    private static Boolean isInInterval(LocalDate value, LocalDate start, LocalDate end) {
        return value.isAfter(start) && !value.isAfter(end);
    }
}
