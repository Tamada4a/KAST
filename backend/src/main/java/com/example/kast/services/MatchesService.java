package com.example.kast.services;


import com.example.kast.controllers.dto.match.PicksDTO;
import com.example.kast.controllers.dto.match.map.MapDTO;
import com.example.kast.controllers.dto.matches.*;
import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.AdminDoc;
import com.example.kast.mongo_collections.documents.TeamDoc;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.mongo_collections.embedded.Matches;
import com.example.kast.mongo_collections.interfaces.AdminRepository;
import com.example.kast.mongo_collections.interfaces.TeamRepository;
import com.example.kast.mongo_collections.interfaces.TournamentRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.kast.utils.MatchUtils.sortMatchesByDateTime;


/**
 * Данный класс является сервисом, реализующим логику обработки всех запросов, связанных со страницей "Матчи"
 *
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @param teamRepository       интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @param adminRepository      интерфейс для взаимодействия с сущностями {@link AdminDoc}
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record MatchesService(TournamentRepository tournamentRepository, TeamRepository teamRepository,
                             AdminRepository adminRepository) {
    /**
     * Метод позволяет получить информацию, необходимую для взаимодействия со страницей "Матчи" на frontend
     *
     * @param player ник игрока, который запрашивает информацию
     * @return Объект класса {@link FullMatchesDTO}, содержащий информацию, необходимую для взаимодействия со страницей
     * "Матчи" на frontend
     */
    public FullMatchesDTO getFullMatches(String player) {
        ArrayList<MatchDTO> ongoingMatches = getAllOngoingMatches();
        ArrayList<MatchTimeByDateDTO> upcomingMatches = getAllUpcomingMatches();

        ArrayList<NameDTO> allEvents = new ArrayList<>();
        tournamentRepository.findAll().forEach((event) -> {
            if (!event.getDateEnd().isBefore(LocalDate.now()))
                allEvents.add(new NameDTO(event.getName()));
        });

        ArrayList<TeamNameTagDTO> teams = new ArrayList<>();
        teamRepository.findAll().forEach((team) -> teams.add(new TeamNameTagDTO(team.getTeamName(), team.getTag())));

        Boolean isAdmin = adminRepository.existsByAdminId(player);

        return new FullMatchesDTO(isAdmin, teams, allEvents, ongoingMatches, upcomingMatches);
    }


    /**
     * Метод позволяет изменить информацию о матче
     *
     * @param editMatchInfoDTO объект класса {@link EditMatchInfoDTO}, содержащий измененную информацию о выбранном матче
     * @param matchId          ID матча, для которого изменяется информация
     * @param event            название турнира, в рамках которого проходит матч
     * @return Объект класса {@link EditMatchInfoDTO}, содержащий измененную информацию о выбранном матче
     * @throws AppException Если в базе данных не существует турнира с таким названием или команды с соответствующим
     *                      названием или тэгом
     */
    public EditMatchInfoDTO editMatchInfo(EditMatchInfoDTO editMatchInfoDTO, int matchId, String event) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Неизвестный турнир", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        ArrayList<Matches> matches = tournamentDoc.getMatches();

        Iterator<Matches> i = matches.iterator();
        while (i.hasNext()) {
            Matches match = i.next();
            if (match.getMatchId() != matchId)
                continue;

            String first = editMatchInfoDTO.getNameFirst();
            String second = editMatchInfoDTO.getNameSecond();

            if (!teamRepository.existsByTag(first) && !teamRepository.existsByTeamName(first))
                throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);

            if (!teamRepository.existsByTag(second) && !teamRepository.existsByTeamName(second))
                throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);

            ArrayList<PicksDTO> picksList = match.getPicks();

            for (PicksDTO pickDTO : picksList) {
                if (pickDTO.getTeam() == null)
                    continue;

                if (pickDTO.getTeam().equals(match.getNameFirst()))
                    pickDTO.setTeam(first);
                else
                    pickDTO.setTeam(second);
            }

            match.setPicks(picksList);

            ArrayList<MapDTO> mapsList = match.getMaps();
            for (MapDTO mapDTO : mapsList) {
                if (mapDTO.getStats() == null)
                    continue;

                if (mapDTO.getStats().getFirstTeam().getName().equals(first)) {
                    mapDTO.getStats().getFirstTeam().setName(first);
                    mapDTO.getStats().getSecondTeam().setName(second);
                } else {
                    mapDTO.getStats().getFirstTeam().setName(second);
                    mapDTO.getStats().getSecondTeam().setName(first);
                }
            }

            if (teamRepository.existsByTag(first)) {
                match.setNameFirst(teamRepository.findByTag(first).getTeamName());
                match.setTagFirst(first);
            } else {
                match.setNameFirst(first);
                match.setTagFirst(teamRepository.findByTeamName(first).getTag());
            }

            if (teamRepository.existsByTag(second)) {
                match.setNameSecond(teamRepository.findByTag(second).getTeamName());
                match.setTagSecond(second);
            } else {
                match.setNameSecond(second);
                match.setTagSecond(teamRepository.findByTeamName(second).getTag());
            }

            String[] dateSplit = editMatchInfoDTO.getDate().split("\\.");

            int year = Integer.parseInt(dateSplit[2]);
            int month = Integer.parseInt(dateSplit[1]);
            int day = Integer.parseInt(dateSplit[0]);

            String[] timeSplit = editMatchInfoDTO.getTime().split(":");

            int hour = Integer.parseInt(timeSplit[0]);
            int minute = Integer.parseInt(timeSplit[1]);

            match.setMatchDate(LocalDateTime.of(year, month, day, hour, minute));

            String newEvent = editMatchInfoDTO.getEvent();

            if (!tournamentRepository.existsByName(newEvent))
                throw new AppException("Неизвестный турнир", HttpStatus.NOT_FOUND);

            if (!newEvent.equals(event)) {
                TournamentDoc tournamentDoc2 = tournamentRepository.findByName(newEvent);
                ArrayList<Matches> matchesList = tournamentDoc2.getMatches();
                matchesList.add(match);

                tournamentDoc2.setMatches(matchesList);
                tournamentRepository.save(tournamentDoc2);

                i.remove();
            }

            break;
        }

        tournamentDoc.setMatches(matches);
        tournamentRepository.save(tournamentDoc);

        return editMatchInfoDTO;
    }


    /**
     * Метод позволяет удалить матч
     *
     * @param matchId ID матча, который необходимо удалить
     * @param event   название турнира, в рамках которого проходит матч
     * @return Объект класса {@link Matches}, содержащий информацию об удаленном матче
     * @throws AppException Если в базе данных не существует турнира с таким названием
     */
    public Matches deleteMatch(int matchId, String event) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Неизвестный турнир", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        AtomicReference<Matches> deletedMatch = new AtomicReference<>();

        ArrayList<Matches> matches = tournamentDoc.getMatches();

        Iterator<Matches> i = matches.iterator();
        while (i.hasNext()) {
            Matches match = i.next();
            if (match.getMatchId() == matchId) {
                deletedMatch.set(match);
                i.remove();
                break;
            }
        }

        tournamentDoc.setMatches(matches);
        tournamentRepository.save(tournamentDoc);

        return deletedMatch.get();
    }


    /**
     * Метод позволяет создать новый матч
     *
     * @param createMatchDTO объект класса {@link CreateMatchDTO}, содержащий информацию о новом матче
     * @return Объект класса {@link Matches}, содержащий информацию о созданном матче
     * @throws AppException Если в базе данных не существует турнира с таким названием или команды с соответствующим
     *                      названием или тэгом
     */
    public Matches createMatch(CreateMatchDTO createMatchDTO) throws AppException {
        String event = createMatchDTO.getEvent();

        if (!tournamentRepository.existsByName(event))
            throw new AppException("Неизвестный турнир", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        if (!teamRepository.existsByTag(createMatchDTO.getLeftTag()))
            throw new AppException("Неизвестная левая команда", HttpStatus.NOT_FOUND);

        if (!teamRepository.existsByTag(createMatchDTO.getRightTag()))
            throw new AppException("Неизвестная правая команда", HttpStatus.NOT_FOUND);

        Matches createdMatch = createMatchDTOToMatches(createMatchDTO);

        ArrayList<Matches> matches = tournamentDoc.getMatches();
        matches.add(createdMatch);

        tournamentDoc.setMatches(matches);
        tournamentRepository.save(tournamentDoc);

        return createdMatch;
    }


    /**
     * Метод приводит полученную JSON-строку, представляющую собой список объектов, содержащих информацию о текущих матчах,
     * к списку объектов класса {@link MatchDTO}, а затем сортирует матчи по времени начала
     *
     * @return Список объектов класса {@link MatchDTO}, содержащих информацию о текущих матчах, отсортированных по
     * времени начала
     */
    private ArrayList<MatchDTO> getAllOngoingMatches() {
        Type listType = new TypeToken<ArrayList<MatchDTO>>() {}.getType();
        ArrayList<MatchDTO> ongoingMatches = new Gson().fromJson(getAllMatchesByType("ongoingMatches"), listType);

        ongoingMatches.sort(Comparator.comparingInt(o -> LocalTime.parse(o.getTime()).toSecondOfDay()));

        return ongoingMatches;
    }


    /**
     * Метод приводит полученную JSON-строку, представляющую собой список объектов, содержащих информацию о будущих матчах,
     * к списку объектов класса {@link MatchTimeByDateDTO}, а затем сортирует матчи по дате начала
     *
     * @return Список объектов класса {@link MatchTimeByDateDTO}, содержащих информацию о ближайших матчах,
     * отсортированных по дате начала
     */
    private ArrayList<MatchTimeByDateDTO> getAllUpcomingMatches() {
        Type listType = new TypeToken<ArrayList<MatchTimeDTO>>() {}.getType();
        ArrayList<MatchTimeDTO> upcomingMatches = new Gson().fromJson(getAllMatchesByType("upcomingMatches"), listType);

        return sortMatchesByDateTime(upcomingMatches, "notreversed");
    }


    /**
     * Метод позволяет получить все ближайшие или текущие матчи
     *
     * @param type тип матчей, который необходимо получить:
     *             <li><b>upcomingMatches</b> - ближайшие матчи</li>
     *             <li><b>ongoingMatches</b> - текущие матчи</li>
     * @return JSON-строка, представляющая собой список объектов, содержащий информацию о всех ближайших или текущих матчах
     */
    private String getAllMatchesByType(String type) {
        ArrayList<Object> matchesList = new ArrayList<>();

        List<TournamentDoc> tournamentDocList = tournamentRepository.findAll();

        for (TournamentDoc tournament : tournamentDocList) {
            LocalDate now = LocalDate.now();
            if (!tournament.getDateEnd().isBefore(now)) {
                if (type.equals("upcomingMatches"))
                    matchesList.addAll(tournament.getUpcomingMatches());
                else
                    matchesList.addAll(tournament.getOngoingMatches());
            }
        }

        return new Gson().toJson(matchesList);
    }


    /**
     * Метод конвертирует объект класса {@link CreateMatchDTO} в объект класса {@link Matches}
     *
     * @param createMatchDTO объект класса {@link CreateMatchDTO}, содержащий информацию о новом матче, который
     *                       необходимо конвертировать в объект класса {@link Matches}
     * @return Объект класса {@link Matches}, содержащий информацию о новом матче для сохранения в базу данных
     */
    private Matches createMatchDTOToMatches(CreateMatchDTO createMatchDTO) {
        return new Matches(createMatchDTO.getMatchId(),
                getDateTimeOfString(createMatchDTO.getDate(), createMatchDTO.getTime()), null,
                createMatchDTO.getLeftTeam(), createMatchDTO.getLeftTag(),
                createMatchDTO.getRightTag(), createMatchDTO.getRightTeam(), createMatchDTO.getTier(),
                createMatchDTO.getMaps(), createMatchDTO.getDescription(), new ArrayList<>(), new ArrayList<>(), "");
    }


    /**
     * Метод переводит строковое представление даты и времени начала матча в обЪект класса {@link LocalDateTime}
     *
     * @param strDate строковое представление даты начала матча в формате дд.мм.гггг
     * @param strTime строковое представление времени начала матча в формате чч:мм
     * @return Объект класса {@link LocalDateTime}, полученный из строкового представления даты и времени начала матча
     */
    private LocalDateTime getDateTimeOfString(String strDate, String strTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return LocalDateTime.of(LocalDate.parse(strDate, dateFormatter), LocalTime.parse(strTime, timeFormatter));
    }
}
