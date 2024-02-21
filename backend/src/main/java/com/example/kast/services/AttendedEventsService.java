package com.example.kast.services;


import com.example.kast.controllers.dto.tab.AttendedEventDTO;
import com.example.kast.controllers.dto.tab.EventInfoDTO;
import com.example.kast.controllers.dto.tab.EventParticipantsDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.documents.TeamDoc;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.mongo_collections.embedded.PrizePlaces;
import com.example.kast.mongo_collections.embedded.Requests;
import com.example.kast.mongo_collections.embedded.Rosters;
import com.example.kast.mongo_collections.interfaces.PlayerRepository;
import com.example.kast.mongo_collections.interfaces.TeamRepository;
import com.example.kast.mongo_collections.interfaces.TournamentRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.kast.utils.PlayerUtils.isInTeam;
import static com.example.kast.utils.Utils.*;


/**
 * Данный класс является сервисом, реализующим логику получения турниров, посещенных пользователем или командой
 *
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @param teamRepository       интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record AttendedEventsService(PlayerRepository playerRepository, TournamentRepository tournamentRepository,
                                    TeamRepository teamRepository) {
    /**
     * Метод приводит полученную JSON-строку, представляющую собой список объектов, содержащий информацию о посещенных
     * пользователем турнирах к классу {@link AttendedEventDTO}
     *
     * @param player ник пользователя, чьи посещенные турниры необходимо получить
     * @return Список объектов класса {@link AttendedEventDTO}, содержащих информацию о посещенных пользователем турнирах
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    public ArrayList<AttendedEventDTO> getPlayerAttendedEvents(String player) throws AppException {
        if (!playerRepository.existsByNick(player))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        Type listType = new TypeToken<ArrayList<AttendedEventDTO>>() {}.getType();
        return new Gson().fromJson(getAttendedEndedPlayerEvents(player, "attendedEvents"), listType);
    }


    /**
     * Метод приводит полученный JSON-строку, представляющую список объектов, содержащих информацию о завершенных турнирах,
     * где пользователь занимал призовые места, к списку объектов класса {@link EventInfoDTO}
     *
     * @param player ник пользователя, чьи завершенные турниры необходимо получить
     * @return Список объектов класса {@link EventInfoDTO}, содержащих информацию о завершенных турнирах, где
     * пользователь занимал призовые места
     */
    public ArrayList<EventInfoDTO> getPlayerEndedEvents(String player) {
        Type listType = new TypeToken<ArrayList<EventInfoDTO>>() {}.getType();
        return new Gson().fromJson(getAttendedEndedPlayerEvents(player, "endedEvents"), listType);
    }


    /**
     * Метод позволяет получить ближайшие и текущие турниры пользователя
     *
     * @param player ник пользователя, чьи ближайшие и текущие турниры необходимо получить
     * @return Список объектов класса {@link EventParticipantsDTO}, содержащих информацию о ближайших и текущих
     * турнирах пользователя со списком участников
     */
    public ArrayList<EventParticipantsDTO> getPlayerUpcomingEvents(String player) {
        PlayerDoc playerDoc = playerRepository.findByNick(player);

        List<TournamentDoc> tournaments = tournamentRepository.findAll();

        AtomicReference<String> team = new AtomicReference<>("");

        ArrayList<Rosters> rosters = playerDoc.getRosters();

        for (Rosters roster : rosters) {
            if (roster.getExitDate() == null) {
                team.set(roster.getTeamName());
                break;
            }
        }

        return getNotEndedEvents(tournaments, team.get(), player);
    }


    /**
     * Метод позволяет получить посещенные командой турниры
     *
     * @param teamName название команды, чьи посещенные турниры необходимо получить
     * @return Список объектов класса {@link AttendedEventDTO}, содержащих информацию о посещенных командой турнирах
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    public ArrayList<AttendedEventDTO> getTeamAttendedEvents(String teamName) throws AppException {
        teamName = replaceDashes(teamName);

        if (!teamRepository.existsByTeamName(teamName))
            throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);


        ArrayList<AttendedEventDTO> attendedEvents = new ArrayList<>();

        List<TournamentDoc> tournaments = tournamentRepository.findAll();

        for (TournamentDoc tournament : tournaments) {
            String eventName = tournament.getName();

            if (!tournament.getEventStatus().equals("ended"))
                continue;

            ArrayList<PrizePlaces> prizePlaces = tournament.getPrizePlaces();
            for (PrizePlaces prizePlace : prizePlaces) {
                if (prizePlace.getTeamName() != null && prizePlace.getTeamName().equals(teamName)) {
                    String dateStart = parseMatchDate(tournament.getDateStart());
                    String dateEnd = parseMatchDate(tournament.getDateEnd());

                    attendedEvents.add(new AttendedEventDTO(
                            eventName,
                            dateStart + " - " + dateEnd,
                            tournament.getPlace(teamName, ""),
                            teamName,
                            tournament.getMvp())
                    );
                    break;
                }
            }
        }

        if (!attendedEvents.isEmpty()) {
            attendedEvents.sort(Comparator.comparingInt(o -> parseStringDateToEpochDays(o.getDate().substring(0, 10))));
            Collections.reverse(attendedEvents);
        }

        return attendedEvents;
    }


    /**
     * Метод позволяет получить завершенные турниры, в которых команда принимал участие и заняла призовые места
     *
     * @param teamName название команды, чьи завершенные турниры необходимо получить
     * @return Список объектов класса {@link EventInfoDTO}, содержащих информацию о завершенных турнирах, где
     * пользователь занимал призовые места
     */
    public ArrayList<EventInfoDTO> getTeamEndedEvents(String teamName) {
        ArrayList<EventInfoDTO> endedEvents = new ArrayList<>();

        List<TournamentDoc> tournaments = tournamentRepository.findAll();

        for (TournamentDoc tournament : tournaments) {
            if (!tournament.getEventStatus().equals("ended"))
                continue;

            String dateStart = parseMatchDate(tournament.getDateStart());
            String dateEnd = parseMatchDate(tournament.getDateEnd());

            String place = tournament.getPlace(teamName, "");

            if (!place.isEmpty()) {
                endedEvents.add(new EventInfoDTO(tournament.getName(), place, dateStart + " - " + dateEnd));
            }
        }

        if (!endedEvents.isEmpty()) {
            endedEvents.sort(Comparator.comparingInt(o -> parseStringDateToEpochDays(o.getDate().substring(0, 10))));
            Collections.reverse(endedEvents);
        }

        return endedEvents;
    }


    /**
     * Метод позволяет получить ближайшие и текущие турниры команды
     *
     * @param teamName название команды, чьи ближайшие и текущие турниры необходимо получить
     * @return Список объектов класса {@link EventParticipantsDTO}, содержащих информацию о ближайших и текущих
     * турнирах команды со списком участников
     */
    public ArrayList<EventParticipantsDTO> getTeamUpcomingEvents(String teamName) {
        return getNotEndedEvents(tournamentRepository.findAll(), teamName, "");
    }


    /**
     * Метод позволяет получить посещенные пользователем турниры или завершенные турниры, в которых пользователь
     * занял призовые места
     *
     * @param player ник пользователя, чьи турниры необходимо получить
     * @param type   тип турниров, который необходимо получить:
     *               <li><b>attendedEvents</b> - посещенные турниры. В данном случае происходит поиск объектов класса
     *               {@link AttendedEventDTO}, содержащих информацию о посещенных турнирах</li>
     *               <li><b>endedEvents</b> - завершенные турниры. В данном случае происходит поиск объектов класса
     *               {@link EventInfoDTO}, содержащих информацию о завершенных турнирах</li>
     * @return JSON-строка, представляющая собой список объектов, содержащих информацию о посещенных пользователем
     * турнирах, либо о завершенных турнирах, в которых пользователь занял призовые места
     */
    private String getAttendedEndedPlayerEvents(String player, String type) {
        PlayerDoc playerDoc = playerRepository.findByNick(player);

        List<TournamentDoc> tournamentDocs = tournamentRepository.findAll();

        ArrayList<Object> playerEvents = new ArrayList<>();

        for (TournamentDoc tournamentDoc : tournamentDocs) {
            ArrayList<PrizePlaces> prizePlaces = tournamentDoc.getPrizePlaces();

            LocalDate dateStart = tournamentDoc.getDateStart();
            LocalDate dateEnd = tournamentDoc.getDateEnd();
            String dateStartStr = parseMatchDate(dateStart);
            String dateEndStr = parseMatchDate(dateEnd);

            for (PrizePlaces prizePlace : prizePlaces) {
                if (prizePlace.getTeamName() == null)
                    continue;

                String teamName = prizePlace.getTeamName();

                if (prizePlace.getTeamName().equals(player)) {
                    if (type.equals("attendedEvents"))
                        playerEvents.add(new AttendedEventDTO(
                                tournamentDoc.getName(),
                                dateStartStr + " - " + dateEndStr,
                                tournamentDoc.getPlace("", player),
                                player, tournamentDoc.getMvp())
                        );
                    else
                        playerEvents.add(new EventInfoDTO(
                                tournamentDoc.getName(),
                                tournamentDoc.getPlace("", player),
                                dateStartStr + " - " + dateEndStr)
                        );
                } else {
                    ArrayList<Rosters> rosters = playerDoc.getRosters();

                    for (Rosters roster : rosters) {
                        if (roster.getTeamName().equals(teamName) &&
                                isInTeam(dateStart, dateEnd, roster.getEnterDate(), roster.getExitDate()) &&
                                (tournamentDoc.getEventStatus().equals("ended"))) {
                            if (type.equals("attendedEvents"))
                                playerEvents.add(new AttendedEventDTO(
                                        tournamentDoc.getName(),
                                        dateStartStr + " - " + dateEndStr,
                                        tournamentDoc.getPlace(teamName, ""),
                                        teamName,
                                        tournamentDoc.getMvp())
                                );
                            else
                                playerEvents.add(new EventInfoDTO(
                                        tournamentDoc.getName(),
                                        tournamentDoc.getPlace(teamName, ""),
                                        dateStartStr + " - " + dateEndStr)
                                );

                            break;
                        }
                    }
                }
            }
        }

        if (!playerEvents.isEmpty()) {
            if (type.equals("attendedEvents"))
                playerEvents.sort(Comparator.comparingInt(o ->
                        parseStringDateToEpochDays(((AttendedEventDTO) o).getDate().substring(0, 10))));
            else
                playerEvents.sort(Comparator.comparingInt(o ->
                        parseStringDateToEpochDays(((EventInfoDTO) o).getDate().substring(0, 10))));
            Collections.reverse(playerEvents);
        }
        return new Gson().toJson(playerEvents);
    }


    /**
     * Метод позволяет получить текущие и будущие турниры, в которых примет участие пользователь или команда
     *
     * @param player      ник игрока, чьи текущие и ближайшие турниры необходимо получить
     * @param teamName    название команды, чьи текущие и ближайшие турниры необходимо получить
     * @param tournaments список сущностей {@link TournamentDoc}, содержащих информацию обо всех турнирах,
     *                    хранящихся в базе данных
     * @return Список объектов класса {@link EventParticipantsDTO}, содержащих информацию о ближайших и текущих турнирах
     * команды или пользователя со списком участников
     */
    private ArrayList<EventParticipantsDTO> getNotEndedEvents(List<TournamentDoc> tournaments, String teamName,
                                                              String player) {
        ArrayList<EventParticipantsDTO> idMatches = new ArrayList<>();

        for (TournamentDoc tournament : tournaments) {
            if (tournament.getEventStatus().equals("ended"))
                continue;

            ArrayList<Requests> tournamentHistoryTeams = tournament.getRequests();

            for (Requests tournamentTeam : tournamentHistoryTeams) {
                if (tournamentTeam.getTeamName() != null &&
                        (tournamentTeam.getTeamName().equals(teamName) ||
                                tournamentTeam.getTeamName().equals(player))) {
                    String dateStart = parseMatchDate(tournament.getDateStart());
                    String dateEnd = parseMatchDate(tournament.getDateEnd());

                    idMatches.add(new EventParticipantsDTO(
                            tournament.getName(),
                            dateStart + " - " + dateEnd,
                            tournamentHistoryTeams)
                    );
                }
            }
        }

        if (!idMatches.isEmpty()) {
            idMatches.sort(Comparator.comparingInt(o -> parseStringDateToEpochDays(o.getDate().substring(0, 10))));
        }

        return idMatches;
    }
}
