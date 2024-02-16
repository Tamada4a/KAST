package com.example.kast.services;


import com.example.kast.controllers.dto.other.FlagNameSrcDTO;
import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.controllers.dto.other.NotificationsDTO;
import com.example.kast.controllers.dto.tab.*;
import com.example.kast.controllers.dto.team.FullTeamDTO;
import com.example.kast.controllers.dto.team.LeftTeamDTO;
import com.example.kast.controllers.dto.team.TeamInfoDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.CountryDoc;
import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.documents.TeamDoc;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.mongo_collections.embedded.Rosters;
import com.example.kast.mongo_collections.interfaces.CountryRepository;
import com.example.kast.mongo_collections.interfaces.PlayerRepository;
import com.example.kast.mongo_collections.interfaces.TeamRepository;
import com.example.kast.mongo_collections.interfaces.TournamentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.kast.utils.MatchUtils.getEventWithMatchesByStatus;
import static com.example.kast.utils.MatchUtils.sortMatchesByDate;
import static com.example.kast.utils.PlayerUtils.getTeam;
import static com.example.kast.utils.Utils.replaceDashes;


/**
 * Данный класс является сервисом, реализующим логику обработки запросов, связанных со страницей команды
 *
 * @param teamRepository         интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @param playerRepository       интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param countryRepository      интерфейс для взаимодействия с сущностями {@link CountryDoc}
 * @param achievementsTabService объект класса {@link AchievementsTabService} - сервис для получения достижений
 *                               пользователя или команды
 * @param attendedEventsService  объект класса {@link AttendedEventsService} - сервис, обрабатывающий запросы для
 *                               получения турниров, посещенных пользователем или командой
 * @param simpMessagingTemplate  объект класса {@link SimpMessagingTemplate}, позволяющий отправлять сообщения сокетам
 * @param tournamentRepository   интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record TeamService(TeamRepository teamRepository, PlayerRepository playerRepository,
                          CountryRepository countryRepository, AchievementsTabService achievementsTabService,
                          AttendedEventsService attendedEventsService, SimpMessagingTemplate simpMessagingTemplate,
                          TournamentRepository tournamentRepository) {
    /**
     * Метод позволяет получить информацию, необходимую для взаимодействия со страницей команды
     *
     * @param teamName название команды, для которой получается информация
     * @param player   ник игрока, который просматривает страницу
     * @return Объект класса {@link FullTeamDTO}, содержащий информацию, необходимую для взаимодействия со страницей
     * команды на frontend
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    public FullTeamDTO getFullTeam(String teamName, String player) throws AppException {
        teamName = replaceDashes(teamName);

        if (!teamRepository.existsByTeamName(teamName))
            throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);

        boolean isParticipant = isParticipant(teamName, player);
        boolean isCaptain = isCaptain(teamName, player);

        TeamInfoDTO teamInfo = getTeamInfo(teamName);

        ArrayList<MatchTabInfoDTO> upcomingMatches = getTeamMatches(teamName, "upcoming");
        ArrayList<MatchTabInfoDTO> endedMatches = getTeamMatches(teamName, "ended");

        ArrayList<EventParticipantsDTO> upcomingEvents = attendedEventsService.getTeamUpcomingEvents(teamName);
        ArrayList<EventInfoDTO> endedEvents = attendedEventsService.getTeamEndedEvents(teamName);

        ArrayList<EventInfoDTO> lanAchievements = achievementsTabService.getTeamAchievements(teamName, "Lan");
        ArrayList<EventInfoDTO> onlineAchievements = achievementsTabService.getTeamAchievements(teamName, "Online");

        ArrayList<FlagNameSrcDTO> exPlayers = getExPlayers(teamName);

        ArrayList<NameDTO> trophies = getTeamTrophies(teamName);

        return new FullTeamDTO(isParticipant, isCaptain, teamInfo, upcomingMatches,
                endedMatches, upcomingEvents, endedEvents, lanAchievements, onlineAchievements, exPlayers, trophies);
    }


    /**
     * Метод позволяет получить список названий турниров, на которых команда занимала первое место
     *
     * @param teamName название команды, для которой необходимо получить список трофеев
     * @return Список объектов класса {@link NameDTO}, содержащих названия турниров, на которых команда занимала первое
     * место
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    public ArrayList<NameDTO> getTeamTrophies(String teamName) throws AppException {
        ArrayList<AttendedEventDTO> attendedEvents = attendedEventsService.getTeamAttendedEvents(teamName);
        ArrayList<NameDTO> teamTrophiesList = new ArrayList<>();
        for (AttendedEventDTO attendedEvent : attendedEvents) {
            if (attendedEvent.getPlace().equals("1")) {
                teamTrophiesList.add(new NameDTO(attendedEvent.getName()));
            }
        }

        return teamTrophiesList;
    }


    /**
     * Метод обрабатывает отправку приглашения в команду пользователю
     *
     * @param team   название команды, от имени которой отправляется приглашение
     * @param player ник игрока, который приглашается в команду
     * @return Строка со значением "Приглашение отправлено успешно"
     * @throws AppException Если пользователя с таким ником или команды с таким названием не существует в базе данных;
     *                      если у приглашаемого пользователя уже есть команда; если приглашаемому игроку было
     *                      отправлено приглашение от имени данной команды
     */
    public String sendTeamInvite(String team, String player) throws AppException {
        if (!playerRepository.existsByNick(player))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        String teamName = replaceDashes(team);
        if (!teamRepository.existsByTeamName(teamName))
            throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);

        PlayerDoc playerDoc = playerRepository.findByNick(player);

        playerDoc.getRosters().forEach((elem) -> {
            if (elem.getExitDate() == null)
                throw new AppException("У данного игрока уже есть команда", HttpStatus.BAD_REQUEST);
        });

        ArrayList<NotificationsDTO> notificationsToSetList = playerDoc.getNotifications();

        notificationsToSetList.forEach((notification) -> {
            if (notification.getDescription().equals("Вас пригласили в команду " + teamName))
                throw new AppException("Вы уже отправили приглашение данному игроку", HttpStatus.BAD_REQUEST);
        });

        notificationsToSetList.add(0, new NotificationsDTO("Вас пригласили в команду " + teamName, "ok"));
        playerDoc.setNotifications(notificationsToSetList);

        playerRepository.save(playerDoc);
        simpMessagingTemplate.convertAndSendToUser(playerDoc.getNick(), "/notifications", notificationsToSetList);

        return "Приглашение отправлено успешно";
    }


    /**
     * Метод обрабатывает решение пользователя по поводу приглашения в команду
     *
     * @param team     название команды, от имени которой отправляется приглашение
     * @param player   ник игрока, который приглашается в команду
     * @param decision решение игрока на тему вступления в команду
     * @return Объект класса {@link NotificationsDTO}, содержащий информацию о решении игрока и тип уведомления
     * @throws AppException Если пользователя с таким ником или команды с таким названием не существует в базе данных;
     *                      если у приглашаемого пользователя уже есть команда
     */
    public NotificationsDTO inviteDecision(String team, String player, String decision) throws AppException {
        if (!playerRepository.existsByNick(player))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        String teamName = replaceDashes(team);
        if (!teamRepository.existsByTeamName(teamName))
            throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);

        PlayerDoc playerDoc = playerRepository.findByNick(player);

        playerDoc.getRosters().forEach((elem) -> {
            if (elem.getExitDate() == null)
                throw new AppException("У данного игрока уже есть команда", HttpStatus.BAD_REQUEST);
        });

        ArrayList<NotificationsDTO> notificationsToSetList = playerDoc.getNotifications();

        notificationsToSetList.removeIf(notification ->
                notification.getDescription().equals("Вас пригласили в команду " + teamName));

        playerDoc.setNotifications(notificationsToSetList);

        if (decision.equals("yes")) {
            ArrayList<Rosters> rosters = playerDoc.getRosters();
            rosters.add(new Rosters(LocalDate.now(), null, teamName));
            playerDoc.setRosters(rosters);
        }

        playerRepository.save(playerDoc);

        if (decision.equals("yes"))
            return new NotificationsDTO("Теперь Вы являетесь частью команды " + teamName, "ok");

        return new NotificationsDTO("Приглашение в команду отклонено", "neutral");
    }


    /**
     * Метод позволяет получить список свободных игроков, которым потенциально можно отправить приглашение в команду
     *
     * @return Список объектов класса {@link NameDTO}, содержащих ники игроков без команды
     */
    public ArrayList<NameDTO> getPlayersWithoutTeams() {
        List<PlayerDoc> playerDocList = playerRepository.findAll();

        ArrayList<NameDTO> allPlayers = new ArrayList<>();
        for (PlayerDoc playerDoc : playerDocList) {
            if (getTeam(playerDoc).isEmpty())
                allPlayers.add(new NameDTO(playerDoc.getNick()));
        }

        return allPlayers;
    }


    /**
     * Метод позволяет получить список активных игроков команды
     *
     * @param teamName название команды, для которой требуется получить список активных игроков
     * @return Список объектов класса {@link FlagNameSrcDTO}, содержащих информацию об активных игроках команды
     */
    public ArrayList<FlagNameSrcDTO> getTeamPlayers(String teamName) {
        ArrayList<FlagNameSrcDTO> playersList = new ArrayList<>();

//        List<PlayerDoc> playerDocList = playerRepository.findAll();
//
//        for (PlayerDoc playerDoc : playerDocList) {
//            ArrayList<Rosters> rosters = playerDoc.getRosters();
//
//            for (Rosters roster : rosters) {
//                if (roster.getTeamName().equals(teamName) && roster.getExitDate() == null) {
//                    String flagPathMini = countryRepository.findByCountryRU(playerDoc.getCountry()).getFlagPathMini();
//                    playersList.add(new FlagNameSrcDTO(playerDoc.getCountry(), playerDoc.getNick(), flagPathMini));
//                    break;
//                }
//            }
//
//            if (playersList.size() == 5) {
//                break;
//            }
//        }

        ArrayList<String> playersNicks = getTeamPlayersNicks(teamName);

        for (String playerNick : playersNicks) {
            PlayerDoc playerDoc = playerRepository.findByNick(playerNick);
            String flagPathMini = countryRepository.findByCountryRU(playerDoc.getCountry()).getFlagPathMini();
            playersList.add(new FlagNameSrcDTO(playerDoc.getCountry(), playerDoc.getNick(), flagPathMini));
        }

        return playersList;
    }


    /**
     * Метод позволяет изменить описание команды
     *
     * @param descDTO объект класса {@link ChangeDescriptionDTO}, содержащий измененное описание соответствующей команды
     * @return Объект класса {@link ChangeDescriptionDTO}, содержащий измененное описание соответствующей команды
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    public ChangeDescriptionDTO changeDescription(ChangeDescriptionDTO descDTO) throws AppException {
        String teamName = replaceDashes(descDTO.getTeam());

        if (!teamRepository.existsByTeamName(teamName))
            throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);

        TeamDoc teamDoc = teamRepository.findByTeamName(teamName);

        teamDoc.setDescription(descDTO.getDescription());

        teamRepository.save(teamDoc);

        return descDTO;
    }


    /**
     * Метод обрабатывает выход игрока из команды. В случае, если игрока исключили из команды - ему отправляется
     * уведомление об этом. Также, если команду покинул капитан, выбирается новый лидер команды
     *
     * @param leftTeamDTO объект класса {@link LeftTeamDTO}, содержащий информацию о выходе или исключении игрока из
     *                    команды
     * @return Объект класса {@link LeftTeamDTO}, содержащий информацию о том, как игрок покинул команду
     * @throws AppException Если пользователя с таким ником или команды с таким названием не существует в базе данных
     */
    public LeftTeamDTO leftTeam(LeftTeamDTO leftTeamDTO) throws AppException {
        String teamName = replaceDashes(leftTeamDTO.getTeam());

        if (!playerRepository.existsByNick(leftTeamDTO.getNick()))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        if (!teamRepository.existsByTeamName(teamName))
            throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);

        PlayerDoc playerDoc = playerRepository.findByNick(leftTeamDTO.getNick());

        ArrayList<Rosters> rosters = playerDoc.getRosters();

        for (Rosters roster : rosters) {
            if (roster.getTeamName().equals(teamName) && roster.getExitDate() == null) {
                roster.setExitDate(LocalDate.now());
            }
        }

        playerDoc.setRosters(rosters);

        if (leftTeamDTO.getIsKick()) {
            ArrayList<NotificationsDTO> notificationsList = new ArrayList<>();
            if (playerDoc.getNotifications() != null)
                notificationsList.addAll(playerDoc.getNotifications());

            notificationsList.add(new NotificationsDTO("Вас исключили из команды " + teamName, "warn"));

            playerDoc.setNotifications(notificationsList);
            simpMessagingTemplate.convertAndSendToUser(playerDoc.getNick(), "/notifications", notificationsList);
        }
        playerRepository.save(playerDoc);

        TeamDoc teamDoc = teamRepository.findByTeamName(teamName);

        List<String> players = getTeamPlayersNicks(teamName);
        players.remove(leftTeamDTO.getNick());

        if (leftTeamDTO.getNick().equals(teamDoc.getCaptain()) && !leftTeamDTO.getIsKick()) {
            String newCap = "";
            if (!players.isEmpty()) {
                int newCapIdx = (int) (Math.random() * players.size());
                newCap = players.get(newCapIdx);

                if (playerRepository.existsByNick(newCap)) {
                    PlayerDoc newCaptainDoc = playerRepository.findByNick(newCap);
                    playerRepository.save(newCaptainDoc);
                }
            }

            teamDoc.setCaptain(newCap);
            teamRepository.save(teamDoc);
        }

        return leftTeamDTO;
    }


    /**
     * Метод позволяет получить все матчи команды (будущие и завершенные), распределив их по соответствующим турнирам
     *
     * @param teamName название команды, чьи матчи необходимо получить
     * @param status   статус матчей, которые необходимо получить:
     *                 <li><b>upcoming</b> - ближайшие матчи</li>
     *                 <li><b>ended</b> - завершенные матчи</li>
     * @return Список объектов класса {@link MatchTabInfoDTO}, соответствующих требуемому статуса и содержащих
     * информацию о матчах соответствующего турнира
     */
    private ArrayList<MatchTabInfoDTO> getTeamMatches(String teamName, String status) {

        List<TournamentDoc> tournamentDocList = tournamentRepository.findAll();

        ArrayList<MatchTabInfoDTO> teamMatchesByEvent = new ArrayList<>();

        for (TournamentDoc tournamentDoc : tournamentDocList) {
            if (tournamentDoc.isEventParticipant(teamName, "")) {
                MatchTabInfoDTO foundedEvent = getEventWithMatchesByStatus(tournamentDoc, teamName, "", status);

                if (foundedEvent.getMatches().size() != 0)
                    teamMatchesByEvent.add(foundedEvent);
            }
        }

        if (!teamMatchesByEvent.isEmpty()) {
            for (MatchTabInfoDTO matchesByEvent : teamMatchesByEvent) {
                matchesByEvent.setMatches(sortMatchesByDate(
                        new ArrayList<>(matchesByEvent.getMatches()),
                        status,
                        tournamentRepository.findByName(matchesByEvent.getEvent()))
                );
            }
        }

        return teamMatchesByEvent;
    }


    /**
     * Метод позволяет получить список ников активных игроков команды
     *
     * @param teamName название команды, для которой требуется получить список активных игроков
     * @return Список ников активных игроков команды
     */
    private ArrayList<String> getTeamPlayersNicks(String teamName) {
        ArrayList<String> players = new ArrayList<>();

        List<PlayerDoc> playerDocList = playerRepository.findAll();
        for (PlayerDoc playerDoc : playerDocList) {
            String team = getTeam(playerDoc);
            if (team.equals(teamName))
                players.add(playerDoc.getNick());

            if (players.size() == 5)
                break;
        }

        return players;
    }


    /**
     * Метод позволяет получить основную информацию о команде
     *
     * @param teamName название команды, о которой необходимо получить информацию
     * @return Объект класса {@link TeamInfoDTO}, содержащий основную информацию о команде
     */
    private TeamInfoDTO getTeamInfo(String teamName) {
        TeamDoc teamDoc = teamRepository.findByTeamName(teamName);

        ArrayList<FlagNameSrcDTO> players = getTeamPlayers(teamName);

        String teamFlagPathMini = countryRepository.findByCountryRU(teamDoc.getCountry()).getFlagPathMini();

        return new TeamInfoDTO(teamDoc.getCity(), teamDoc.getCountry(), teamDoc.getDescription(), teamDoc.getTop(), teamName, teamFlagPathMini, players);
    }


    /**
     * Метод позволяет определить, является ли пользователь участником команды
     *
     * @param teamName название команды, в которую потенциально входит пользователь
     * @param player   ник пользователя, для которого необходимо определить является ли он участников команды
     * @return Является ли пользователь участником команды: <code>true</code>, если является; <code>false</code> иначе
     */
    private Boolean isParticipant(String teamName, String player) {
        if (!playerRepository.existsByNick(player))
            return false;

        PlayerDoc playerDoc = playerRepository.findByNick(player);

        return getTeam(playerDoc).equals(teamName);
    }


    /**
     * Метод позволяет определить, является ли пользователь капитаном команды
     *
     * @param teamName название команды, для которой требуется определить, является ли пользователь капитаном команды
     * @param player   ник пользователя, для которого необходимо определить является ли он капитаном команды
     * @return Является ли пользователь капитаном команды: <code>true</code>, если является; <code>false</code> иначе
     */
    private Boolean isCaptain(String teamName, String player) {
        if (!playerRepository.existsByNick(player))
            return false;

        TeamDoc teamDoc = teamRepository.findByTeamName(teamName);
        return teamDoc.getCaptain().equals(player);
    }


    /**
     * Метод позволяет получить список бывших игроков команды
     *
     * @param teamName название команды, для которой требуется получить список бывших игроков
     * @return Список объектов класса {@link FlagNameSrcDTO}, содержащих информацию о бывших игроках команды
     */
    private ArrayList<FlagNameSrcDTO> getExPlayers(String teamName) {

        List<PlayerDoc> playerDocList = playerRepository.findAll();

        ArrayList<FlagNameSrcDTO> exPlayers = new ArrayList<>();

        for (PlayerDoc playerDoc : playerDocList) {
            ArrayList<Rosters> playerRosters = playerDoc.getRosters();

            for (Rosters roster : playerRosters) {
                if (roster.getTeamName().equals(teamName) && roster.getExitDate() != null && !isContainsPlayer(exPlayers, playerDoc, teamName)) {
                    exPlayers.add(new FlagNameSrcDTO(
                            playerDoc.getCountry(),
                            playerDoc.getNick(),
                            countryRepository.findByCountryRU(playerDoc.getCountry()).getFlagPathMini())
                    );
                }
            }
        }

        return exPlayers;
    }


    /**
     * Метод позволяет определить, содержит ли список бывших игроков интересующего нас игрока
     *
     * @param exPlayers список объектов класса {@link FlagNameSrcDTO}, содержащих информацию о бывших игроках команды
     * @param playerDoc объект класса {@link PlayerDoc}, содержащий информацию об игроке, который проверяется на
     *                  вхождение в список бывших игроков команды
     * @param teamName  название команды, для которой составляется список бывших игроков
     * @return Содержит ли список бывших игроков интересующего нас игрока: <code>true</code>, если является;
     * <code>false</code> иначе
     */
    private Boolean isContainsPlayer(ArrayList<FlagNameSrcDTO> exPlayers, PlayerDoc playerDoc, String teamName) {
//        ArrayList<Rosters> playerRosters = playerDoc.getRosters();
//        for (Rosters roster : playerRosters) {
//            if (roster.getTeamName().equals(teamName) && roster.getExitDate() == null)
//                return true;
//        }
//        if(getTeam(playerDoc).equals(teamName))
//            return true;

        return exPlayers.contains(
                new FlagNameSrcDTO(playerDoc.getCountry(), playerDoc.getNick(),
                        countryRepository.findByCountryRU(playerDoc.getCountry()).getFlagPathMini())
        );
    }
}
