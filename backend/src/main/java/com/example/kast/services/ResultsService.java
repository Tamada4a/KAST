package com.example.kast.services;


import com.example.kast.controllers.dto.matches.MatchTimeByDateDTO;
import com.example.kast.controllers.dto.matches.MatchTimeDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.documents.TeamDoc;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.mongo_collections.embedded.Rosters;
import com.example.kast.mongo_collections.interfaces.PlayerRepository;
import com.example.kast.mongo_collections.interfaces.TeamRepository;
import com.example.kast.mongo_collections.interfaces.TournamentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.kast.utils.MatchUtils.getAttendedEndedMatches;
import static com.example.kast.utils.MatchUtils.sortMatchesByDateTime;
import static com.example.kast.utils.PlayerUtils.isInTeam;
import static com.example.kast.utils.Utils.replaceDashes;


/**
 * Данный класс является сервисом, реализующим логику обработки запросов для получения результатов всех матчей
 *
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param teamRepository       интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record ResultsService(TournamentRepository tournamentRepository, PlayerRepository playerRepository,
                             TeamRepository teamRepository) {
    /**
     * Метод позволяет получить результаты всех матчей
     *
     * @return Список объектов класса {@link MatchTimeByDateDTO}, содержащих информацию о прошедших матчах, соответствующих определенной дате
     */
    public ArrayList<MatchTimeByDateDTO> getAllResults() {
        List<TournamentDoc> tournamentDocList = tournamentRepository.findAll();

        ArrayList<MatchTimeDTO> resultsList = new ArrayList<>();

        for (TournamentDoc tournamentDoc : tournamentDocList) {
            resultsList.addAll(getAttendedEndedMatches(tournamentDoc, "", ""));
        }

        return sortMatchesByDateTime(resultsList, "reversed");
    }


    /**
     * Метод позволяет получить результаты всех матчей пользователя, отсортированных по дате начала
     *
     * @param player ник игрока, чьи результаты необходимо получить
     * @return Список объектов класса {@link MatchTimeByDateDTO}, содержащих информацию о прошедших матчах игрока,
     * соответствующих определенной дате
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    public ArrayList<MatchTimeByDateDTO> getPlayerResults(String player) throws AppException {
        if (!playerRepository.existsByNick(player))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        PlayerDoc playerDoc = playerRepository.findByNick(player);

        ArrayList<MatchTimeDTO> resultsList = new ArrayList<>();

        List<TournamentDoc> tournamentDocs = tournamentRepository.findAll();

        for (TournamentDoc tournamentDoc : tournamentDocs) {
            LocalDate dateStart = tournamentDoc.getDateStart();
            LocalDate dateEnd = tournamentDoc.getDateEnd();

            ArrayList<Rosters> rosters = playerDoc.getRosters();

            if (tournamentDoc.getParticipantType().equals("player") && tournamentDoc.isEventParticipant("", playerDoc.getNick())) {
                resultsList.addAll(getAttendedEndedMatches(tournamentDoc, player, ""));
            } else {
                for (Rosters roster : rosters) {
                    if (tournamentDoc.isEventParticipant(roster.getTeamName(), "") && isInTeam(dateStart, dateEnd, roster.getEnterDate(), roster.getExitDate())) {
                        resultsList.addAll(getAttendedEndedMatches(tournamentDoc, "", roster.getTeamName()));
                    }
                }
            }
        }

        return sortMatchesByDateTime(resultsList, "reversed");
    }


    /**
     * Метод позволяет получить результаты всех матчей команды, отсортированных по дате начала
     *
     * @param teamName название команды, чьи результаты необходимо получить
     * @return Список объектов класса {@link MatchTimeByDateDTO}, содержащих информацию о прошедших матчах команды,
     * соответствующих определенной дате
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    public ArrayList<MatchTimeByDateDTO> getTeamResults(String teamName) {
        teamName = replaceDashes(teamName);

        if (!teamRepository.existsByTeamName(teamName))
            throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);

        List<TournamentDoc> tournamentDocList = tournamentRepository.findAll();

        ArrayList<MatchTimeDTO> resultsList = new ArrayList<>();

        for (TournamentDoc tournamentDoc : tournamentDocList) {
            if (tournamentDoc.isEventParticipant(teamName, "")) {
                resultsList.addAll(getAttendedEndedMatches(tournamentDoc, "", teamName));
            }
        }

        return sortMatchesByDateTime(resultsList, "reversed");
    }
}
