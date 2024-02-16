package com.example.kast.services;


import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.controllers.dto.tab.RosterDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.mongo_collections.embedded.Rosters;
import com.example.kast.mongo_collections.interfaces.PlayerRepository;
import com.example.kast.mongo_collections.interfaces.TournamentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.example.kast.utils.PlayerUtils.isInTeam;
import static com.example.kast.utils.Utils.getMonthName;
import static java.time.temporal.ChronoUnit.DAYS;


/**
 * Данный класс является сервисом, реализующим логику получения информации о командах пользователя
 *
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @param teamService          объект класса {@link TeamService} - сервис, обрабатывающий запросы, приходящие со страницы
 *                             команды
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record RostersService(PlayerRepository playerRepository, TournamentRepository tournamentRepository,
                             TeamService teamService) {
    /**
     * Метод позволяет получить информацию о всех командах, в которых состоял или состоит пользователь
     *
     * @param player ник пользователя, о чьих командах необходимо получить информацию
     * @return Список объектов класса {@link RosterDTO}, содержащих информацию о всех командах, в которых состоял или
     * состоит пользователь
     * @throws AppException Если в базе данных не существует команды с запрашиваемым названием
     */
    public ArrayList<RosterDTO> getPlayerRosters(String player) throws AppException {
        PlayerDoc playerDoc = playerRepository.findByNick(player);

        ArrayList<Rosters> rosters = playerDoc.getRosters();
        Collections.reverse(rosters);

        ArrayList<RosterDTO> playerRosters = new ArrayList<>();

        for (Rosters roster : rosters) {
            LocalDate enterDate = roster.getEnterDate();
            LocalDate exitDate = roster.getExitDate();

            String period = getPeriod(enterDate) + " - " + getPeriod(exitDate);

            long dayDiff = getDayDiff(enterDate, exitDate);

            ArrayList<NameDTO> trophies = getPlayerTrophiesInRoster(roster);

            playerRosters.add(new RosterDTO(roster.getTeamName(), period, trophies, dayDiff));
        }

        return playerRosters;
    }


    /**
     * Метод позволяет получить список названий трофеев игрока за время игры в команде
     *
     * @param roster объект класса {@link Rosters}, содержащий информацию о команде игрока, за время игры в которой
     *               необходимо получить список трофеев
     * @return Список объектов класса {@link NameDTO}, содержащих названия турниров, которые игрок выигрывал в составе
     * данной команды
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    private ArrayList<NameDTO> getPlayerTrophiesInRoster(Rosters roster) throws AppException {
        ArrayList<NameDTO> trophies = teamService.getTeamTrophies(roster.getTeamName());

        ArrayList<NameDTO> playerTrophiesInRoster = new ArrayList<>();

        for (NameDTO trophy : trophies) {
            TournamentDoc tournamentDoc = tournamentRepository.findByName(trophy.getName());

            LocalDate startDate = tournamentDoc.getDateStart();
            LocalDate endDate = tournamentDoc.getDateEnd();

            LocalDate rosterExitDate = roster.getExitDate();
            LocalDate rosterEnterDate = roster.getEnterDate();

            if (isInTeam(startDate, endDate, rosterEnterDate, rosterExitDate)) {
                playerTrophiesInRoster.add(trophy);
            }
        }

        return playerTrophiesInRoster;
    }


    /**
     * Метод позволяет получить количество дней, проведенных игроком в команде
     *
     * @param enterDate дата вступления игрока в команду
     * @param exitDate  дата выхода игрока из команды. Может быть <code>null</code>, если игрок на данный момент
     *                  находится в команде
     * @return Количество дней, проведенных игроком в команде
     */
    private long getDayDiff(LocalDate enterDate, LocalDate exitDate) {
        return DAYS.between(enterDate, Objects.requireNonNullElseGet(exitDate, LocalDate::now));
    }


    /**
     * Метод позволяет получить период пребывания игрока в команде в формате <i>Месяц год</i>
     *
     * @param date дата, которую необходимо представить в формате <i>Месяц год</i>. Может быть <code>null</code>, если
     *             в данный момент игрок находится в команде
     * @return Если дата была <code>null</code> - возвращается "Настоящее время", иначе Строка формата <i>Месяц год</i>
     */
    private String getPeriod(LocalDate date) {
        if (date == null)
            return "Настоящее время";

        return getMonthName(date.getMonthValue()) + " " + date.getYear();
    }
}
