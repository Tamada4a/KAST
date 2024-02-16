package com.example.kast.services;


import com.example.kast.controllers.dto.match.*;
import com.example.kast.controllers.dto.match.map.FullMatchMapDTO;
import com.example.kast.controllers.dto.match.map.LogDTO;
import com.example.kast.controllers.dto.match.map.MapDTO;
import com.example.kast.controllers.dto.match.map.MapStatsDTO;
import com.example.kast.controllers.dto.match.twitch.TwitchCodeResponseDTO;
import com.example.kast.controllers.dto.match.twitch.TwitchViewersResponseDTO;
import com.example.kast.controllers.dto.match.youtube.YouTubeViewersItemDTO;
import com.example.kast.controllers.dto.match.youtube.YouTubeViewersResponseDTO;
import com.example.kast.controllers.dto.other.FlagNameSrcDTO;
import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.enums.LogType;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.*;
import com.example.kast.mongo_collections.embedded.Matches;
import com.example.kast.mongo_collections.embedded.Requests;
import com.example.kast.mongo_collections.embedded.Rosters;
import com.example.kast.mongo_collections.interfaces.*;
import com.example.kast.utils.LogParser;
import com.google.gson.Gson;
import generator.RandomUserAgentGenerator;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.kast.utils.MatchUtils.getCurrentMapByMatchId;
import static com.example.kast.utils.PlayerUtils.getTeam;
import static com.example.kast.utils.Utils.getProperty;
import static com.example.kast.utils.Utils.replaceDashes;


/**
 * Данный класс является сервисом, реализующим логику обработки всех запросов, связанных со страницей матча
 *
 * @param tournamentRepository  интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @param playerRepository      интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param countryRepository     интерфейс для взаимодействия с сущностями {@link CountryDoc}
 * @param teamRepository        интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @param adminRepository       интерфейс для взаимодействия с сущностями {@link AdminDoc}
 * @param mapPoolRepository     интерфейс для взаимодействия с сущностями {@link MapPoolDoc}
 * @param simpMessagingTemplate объект класса {@link SimpMessagingTemplate}, позволяющий отправлять сообщения сокетам
 * @param weaponRepository      интерфейс для взаимодействия с сущностями {@link WeaponDoc}
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record MatchService(TournamentRepository tournamentRepository, PlayerRepository playerRepository,
                           CountryRepository countryRepository, TeamRepository teamRepository,
                           AdminRepository adminRepository, MapPoolRepository mapPoolRepository,
                           SimpMessagingTemplate simpMessagingTemplate, WeaponRepository weaponRepository) {
    /**
     * Метод позволяет получить необходимую информацию для взаимодействия со страницей матча на frontend
     *
     * @param matchId ID матча, для которого запрашивается информация
     * @param player  ник игрока, который запрашивает информацию
     * @return Объект класса {@link FullMatchDTO}, содержащий информацию для взаимодействия со страницей матча на
     * frontend
     * @throws AppException Если в базе данных не существует матча с таким ID
     */
    public FullMatchDTO getFullMatch(int matchId, String player) throws AppException {
        List<TournamentDoc> tournamentDocList = tournamentRepository.findAll();

        String eventName = "";

        Matches match_ = null;

        for (TournamentDoc tournamentDoc : tournamentDocList) {
            match_ = tournamentDoc.getMatchById(matchId);

            if (match_ != null) {
                eventName = tournamentDoc.getName();
                break;
            }
        }

        if (eventName.isEmpty())
            throw new AppException("Матча с таким ID не существует", HttpStatus.BAD_REQUEST);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(eventName);

        String partType = tournamentDoc.getFormat().equals("1x1") ? "player" : "team";

        ChosenMatchDTO chosenMatch = matchesToChosenMatchDTO(match_, tournamentDoc, partType);

        List<CountryDoc> countries = countryRepository.findAll();

        ArrayList<FullMatchTeamDTO> teams = getAllAcceptedParticipants(tournamentDoc, partType);

        ArrayList<NameDTO> allEvents = getAllEvents(tournamentDocList);

        boolean isParticipant = getIsParticipant(player, tournamentDoc.getRequests());

        boolean isAdmin = adminRepository.existsByAdminId(player);

        return new FullMatchDTO(chosenMatch, countries, teams, allEvents, isParticipant, isAdmin);
    }


    /**
     * Метод позволяет получить список стримов выбранного матча
     *
     * @param matchId ID матча, для которого запрашивается информация
     * @return Список объектов класса {@link StreamFlagDTO}, содержащих информацию о прямых трансляциях, освещающих
     * выбранный матч
     * @throws AppException Если в базе данных не существует матча с таким ID
     */
    public ArrayList<StreamFlagDTO> getMatchStreams(int matchId) throws AppException {
        List<TournamentDoc> tournamentDocList = tournamentRepository.findAll();

        Matches match_ = null;

        for (TournamentDoc tournamentDoc : tournamentDocList) {
            match_ = tournamentDoc.getMatchById(matchId);
            if (match_ != null) {
                break;
            }
        }

        if (match_ == null)
            throw new AppException("Матча с таким ID не существует", HttpStatus.BAD_REQUEST);

        return match_.getStreams() != null ? getStreams(match_.getStreams()) : null;
    }


    /**
     * Метод позволяет редактировать информацию о матче - описание, пики/баны
     *
     * @param matchId        ID матча, для которого редактируется информации
     * @param chosenMatchDTO объект класса {@link ChosenMatchDTO}, содержащий измененную информацию о выбранном матче
     * @return Объект класса {@link ChosenMatchDTO}, содержащий всю информацию о выбранном матче
     * @throws AppException Если в базе данных не существует матча с таким ID
     */
    public ChosenMatchDTO editMatchDesc(int matchId, ChosenMatchDTO chosenMatchDTO) throws AppException {
        TournamentDoc tournamentDoc = tournamentRepository.findByName(chosenMatchDTO.getEvent());

        Matches match = tournamentDoc.getMatchById(matchId);

        if (match == null)
            throw new AppException("Матча с таким ID не существует", HttpStatus.NOT_FOUND);

        match.setPicks(chosenMatchDTO.getPicks());
        match.setMaps(fullMatchMapDTOListToMapDTOList(chosenMatchDTO.getMaps(), match.getMaps()));
        match.setDescription(chosenMatchDTO.getDescription());

        tournamentDoc.editMatchList(match);
        tournamentRepository.save(tournamentDoc);

        return chosenMatchDTO;
    }


    /**
     * Метод позволяет редактировать прямые трансляции, освещающие матч; изменять IP-адрес сервера, на котором проходит
     * матч
     *
     * @param event               название турнира, в рамках которого проходит матч
     * @param matchId             ID матча, для которого изменяется список стримов и IP-адрес
     * @param editMatchStreamsDTO объект класса {@link EditMatchStreamsDTO}, содержащий информацию об измененных стримах
     *                            и IP-адресе сервера
     * @return Список объектов класса {@link StreamFlagDTO}, содержащих информацию об измененных стримах, освещающих
     * выбранный матч
     * @throws AppException Если турнира с таким названием не существует в базе данных или матча с запрашиваемым ID
     */
    public ArrayList<StreamFlagDTO> editMatchStreams(String event, int matchId, EditMatchStreamsDTO editMatchStreamsDTO) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.BAD_REQUEST);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        Matches match = tournamentDoc.getMatchById(matchId);

        if (match == null)
            throw new AppException("Матча с таким ID не существует", HttpStatus.BAD_REQUEST);

        match.setStreams(editMatchStreamsDTO.getStreams());
        match.setIp(editMatchStreamsDTO.getIp());

        tournamentDoc.editMatchList(match);
        tournamentRepository.save(tournamentDoc);

        return getStreams(match.getStreams());
    }


    /**
     * Метод предобрабатывает входящие логи, а затем их парсит. При необходимости отправляет сообщения сокетам,
     * соответствующим(ему) типу логов
     *
     * @param input   входящие логи
     * @param event   название турнира, в рамках которого проходит матч
     * @param matchId ID матча, для которого парсятся логи
     * @throws AppException Если в базе данных не существует: турнира с таким названием; матча с таким ID; команды с
     *                      таким названием
     */
    public void parseLogs(String input, String event, int matchId) {
        event = replaceDashes(event);

        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        if (tournamentRepository.findByName(event).getMatchById(matchId) == null)
            throw new AppException("Матча с таким ID не существует", HttpStatus.NOT_FOUND);

        LogParser logParser = new LogParser(event, matchId, tournamentRepository, playerRepository,
                mapPoolRepository, teamRepository, weaponRepository);

        LogType type = LogType.NONE;

        String[] inputArray = input.split("\n");
        for (int i = 0; i < inputArray.length; ++i) {
            if (inputArray[i].contains("Team playing \"CT\":")) {
                String new_ct = logParser.getMatchSubstring(": Team playing \"CT\": (.*)", inputArray[i]);
                if (!teamRepository.existsByTag(new_ct))
                    throw new AppException("Такой команды не существует", HttpStatus.NOT_FOUND);
            } else if (inputArray[i].contains("Team playing \"TERRORIST\":")) {
                String new_t = logParser.getMatchSubstring(": Team playing \"TERRORIST\": (.*)", inputArray[i]);
                if (!teamRepository.existsByTag(new_t))
                    throw new AppException("Такой команды не существует", HttpStatus.NOT_FOUND);
                break;
            } else if (inputArray[i].contains("killed") && inputArray[i].contains("with")) {
                String temp = inputArray[i];
                if (i + 1 < inputArray.length) {
                    if ((inputArray[i + 1].contains("flash-assisted killing")) || (inputArray[i + 1].contains("assisted killing") && !inputArray[i + 1].contains("flash-assisted killing"))) {
                        temp += ". " + inputArray[i + 1];
                        ++i;
                    }
                }
                if (i + 2 < inputArray.length) {
                    if ((inputArray[i + 2].contains("flash-assisted killing")) || (inputArray[i + 2].contains("assisted killing") && !inputArray[i + 2].contains("flash-assisted killing"))) {
                        temp += ". " + inputArray[i + 2];
                        ++i;
                    }
                }
                type = logParser.parse(temp);
            } else if (inputArray[i].contains(">\" picked up \"")) {
                String temp = inputArray[i];
                if (i + 1 < inputArray.length) {
                    if (inputArray[i + 1].contains("purchased")) {
                        temp = inputArray[i + 1];
                        ++i;
                    }
                }
                type = logParser.parse(temp);
            } else if (inputArray[i].contains("was killed by the bomb")) {
                String temp = inputArray[i];
                if (i + 1 < inputArray.length) {
                    if (inputArray[i + 1].contains("committed suicide with \"world\""))
                        ++i;
                }
                type = logParser.parse(temp);
            } else
                type = logParser.parse(inputArray[i]);


            if (type == LogType.LOGS || type == LogType.SCORE_LOGS_SCOREBOARD || type == LogType.SCOREBOARD_LOGS)
                simpMessagingTemplate.convertAndSend("/match/logs/" + matchId, getLogs(event, matchId));

            if (type == LogType.MAPNAME) {
                MapDTO currentMap = getCurrentMap(event, matchId);
                if (currentMap != null)
                    simpMessagingTemplate.convertAndSend("/match/logsMapName/" + matchId, currentMap.getMapName());
            }

            if (type == LogType.SCOREBOARD || type == LogType.SCOREBOARD_LOGS || type == LogType.SCORE_LOGS_SCOREBOARD || type == LogType.SCORE_SCOREBOARD || type == LogType.MAPNAME) {
                ScoreBoardDTO scoreBoardDTO = getScoreboard(event, matchId);
                if (scoreBoardDTO != null)
                    simpMessagingTemplate.convertAndSend("/match/scoreBoard/" + matchId, scoreBoardDTO);
            }

            if (type == LogType.SCORE_LOGS_SCOREBOARD || type == LogType.SCORE_SCOREBOARD) {
                MatchScoreDTO matchScoreDTO = getMatchFullScore(matchId, event);
                if (matchScoreDTO != null)
                    simpMessagingTemplate.convertAndSend("/match/score/" + matchId, matchScoreDTO);
            }
        }
    }


    /**
     * Метод позволяет получить список логов выбранного матча
     *
     * @param event   название турнира, в рамках которого проходит матч
     * @param matchId ID матча, для которого запрашивается информация
     * @return Список объектов класса {@link LogDTO}, содержащих информацию о логах, приходящих с сервера
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    public ArrayList<LogDTO> getLogs(String event, int matchId) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        Matches match = tournamentRepository.findByName(event).getMatchById(matchId);

        ArrayList<LogDTO> logs = new ArrayList<>();

        if (match == null)
            return logs;

        ArrayList<MapDTO> maps = match.getMaps();
        Collections.reverse(maps);
        for (MapDTO map : maps) {
            if (!map.getStatus().equals("upcoming") && map.getLogs() != null) {
                logs.addAll(map.getLogs());
            }
        }

        return logs;
    }


    /**
     * Метод позволяет получить информацию, отображаемую в таблице (scoreboard) на странице матча
     *
     * @param event   название турнира, в рамках которого проходит матч
     * @param matchId ID матча, для которого запрашивается информация
     * @return Если текущая карта <code>null</code> - <code>null</code>, иначе объект класса {@link ScoreBoardDTO},
     * описывающий таблицу на странице матча
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    public ScoreBoardDTO getScoreboard(String event, int matchId) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        MapDTO map = getCurrentMap(event, matchId);

        if (map == null)
            return null;

        return new ScoreBoardDTO(map.getStats(), map.getCurrentRound(),
                getRoundsHistory(map.getLogs()), map.getStatus(), map.getMapName());
    }


    /**
     * Метод позволяет получить текущую карту запрашиваемого матча
     *
     * @param event   название турнира, в рамках которого проходит матч
     * @param matchId ID матча, для которого запрашивается информация
     * @return Название активной карты. Может быть <code>TBA</code>, если ни одна карта не играется
     */
    public MapDTO getCurrentMap(String event, int matchId) {
        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        return getCurrentMapByMatchId(tournamentDoc, matchId);
    }


    /**
     * Метод позволяет получить полный счет в матче: по картам и на текущей карте
     *
     * @param matchId ID матча, для которого запрашивается информация
     * @param event   название турнира, в рамках которого проходит матч
     * @return Если матч с таким ID не найден - <code>null</code>, иначе объект класса {@link MatchScoreDTO}, содержащий
     * информацию о счете матча на текущей карте и счете по картам
     */
    private MatchScoreDTO getMatchFullScore(int matchId, String event) {
        Matches match = tournamentRepository.findByName(event).getMatchById(matchId);
        if (match == null)
            return null;

        ArrayList<Integer> mapsScore = match.getScoreByMap();
        ArrayList<Integer> curScore = match.getCurrentScoreOnMap();

        return new MatchScoreDTO(curScore.get(0), curScore.get(1), mapsScore.get(0), mapsScore.get(1));
    }


    /**
     * Метод позволяет получить историю раундов на текущей карте
     *
     * @param logs список объектов класса {@link LogDTO}, содержащих информацию о логах на текущей карте
     * @return Список объектов класса {@link RoundHistoryDTO}, содержащих информацию о победителях и типе победы в
     * каждом раунде
     */
    private ArrayList<RoundHistoryDTO> getRoundsHistory(ArrayList<LogDTO> logs) {
        ArrayList<RoundHistoryDTO> roundsHistory = new ArrayList<>();

        if (logs == null)
            return roundsHistory;

        for (LogDTO log : logs) {
            if (log.getType().equals("roundEnd")) {
                roundsHistory.add(new RoundHistoryDTO(
                        log.getWinner(),
                        getHowRoundWon(log.getHow())
                ));
            }
        }

        Collections.reverse(roundsHistory);

        return roundsHistory;
    }


    /**
     * Метод переводит способ победы в раунде в тип иконки, соответствующий способу
     *
     * @param howRoundWon строка, описывающая как закончился раунд
     * @return Тип иконки, соответствующий способу победы в раунде
     */
    private String getHowRoundWon(String howRoundWon) {
        return switch (howRoundWon) {
            case "Враги уничтожены" -> "Skull";
            case "Взорвана бомба" -> "BombExploded";
            case "Время истекло" -> "Timer";
            case "Бомба обезврежена" -> "BombDefused";
            default -> "";
        };
    }


    /**
     * Метод перевозит список объектов класса {@link FullMatchMapDTO} в список объектов класса {@link MapDTO}
     *
     * @param fullMatchMapDTOList список объектов класса {@link FullMatchMapDTO}, содержащих информацию о картах матча
     *                            со статистикой, которые необходимо перевести в список объектов класса {@link MapDTO}
     * @param mapDTOList          список объектов класса {@link MapDTO}, содержащих полную информацию о текущих картах
     *                            матча. Используется с целью сохранения информации о карте после ее изменения
     * @return Список объектов класса {@link MapDTO}, содержащих обновленную информацию о картах матча
     */
    private ArrayList<MapDTO> fullMatchMapDTOListToMapDTOList(ArrayList<FullMatchMapDTO> fullMatchMapDTOList,
                                                              ArrayList<MapDTO> mapDTOList) {
        ArrayList<MapDTO> newMapDTOList = new ArrayList<>();

        for (FullMatchMapDTO map : fullMatchMapDTOList) {
            ArrayList<LogDTO> logs = new ArrayList<>();
            MapStatsDTO stats = null;
            Integer curRound = null;

            for (MapDTO matchMap : mapDTOList) {
                if (matchMap.getMapName().equals(map.getMapName())) {
                    if (matchMap.getCurrentRound() != null)
                        curRound = matchMap.getCurrentRound();

                    if (matchMap.getLogs() != null)
                        logs = matchMap.getLogs();

                    if (matchMap.getStats() != null)
                        stats = matchMap.getStats();

                    break;
                }
            }
            newMapDTOList.add(new MapDTO(
                    map.getMapName(),
                    map.getStatus(),
                    logs,
                    stats,
                    map.getFirstHalf(),
                    map.getSecondHalf(),
                    map.getOvertime(),
                    curRound
            ));
        }

        return newMapDTOList;
    }


    /**
     * Метод позволяет определить, является ли пользователь участником матча
     *
     * @param player   ник пользователя, о котором необходимо получить информацию
     * @param requests список объектов класса {@link Requests}, содержащих информацию о заявках на турнир
     * @return Является ли пользователь участником матча: <code>true</code>, если является; <code>false</code> иначе
     */
    private Boolean getIsParticipant(String player, ArrayList<Requests> requests) {
        if (!playerRepository.existsByNick(player))
            return false;

        String team = getTeam(playerRepository.findByNick(player));

        for (Requests request : requests) {
            if (request.getTeamName().equals(player) || request.getTeamName().equals(team))
                return true;
        }

        return false;
    }


    /**
     * Метод позволяет получить список названий всех турниров
     *
     * @param tournamentDocList список объектов класса {@link TournamentDoc}, содержащий информацию обо всех турнирах,
     *                          хранящихся в базе данных
     * @return Список объектов класса {@link NameDTO}, содержащих названия всех турниров, хранящихся в базе данных
     */
    private ArrayList<NameDTO> getAllEvents(List<TournamentDoc> tournamentDocList) {
        ArrayList<NameDTO> allEvents = new ArrayList<>();

        for (TournamentDoc tournamentDoc : tournamentDocList) {
            allEvents.add(new NameDTO(tournamentDoc.getName()));
        }

        return allEvents;
    }


    /**
     * Метод позволяет получить всю информацию, необходимую для отображения, о команде и ее игроках
     *
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      проходит матч
     * @param partType      тип участников турнира:
     *                      <li><b>player</b> - игрок, если формат турнира 1x1</li>
     *                      <li><b>team</b> - команда, если формат турнира 2x2 или 5x5</li>
     * @return Список объектов класса {@link FullMatchTeamDTO}, содержащий информацию о принятых участниках турнира
     */
    private ArrayList<FullMatchTeamDTO> getAllAcceptedParticipants(TournamentDoc tournamentDoc, String partType) {
        ArrayList<Requests> requests = tournamentDoc.getRequests();

        ArrayList<FullMatchTeamDTO> allParticipants = new ArrayList<>();
        for (Requests request : requests) {
            if (request.getStatus().equals("accepted")) {
                allParticipants.add(getFullParticipant(request.getTeamName(), partType, 0));
            }
        }

        return allParticipants;
    }


    /**
     * Метод конвертирует объект класса {@link Matches} в объект класса {@link ChosenMatchDTO}
     *
     * @param match         объект класса {@link Matches}, содержащий информацию о матче, который необходимо
     *                      конвертировать в объект класса {@link ChosenMatchDTO}
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      проходит матч
     * @param partType      тип участников турнира:
     *                      <li><b>player</b> - игрок, если формат турнира 1x1</li>
     *                      <li><b>team</b> - команда, если формат турнира 2x2 или 5x5</li>
     * @return Объект класса {@link ChosenMatchDTO}, содержащий информацию о матче
     */
    private ChosenMatchDTO matchesToChosenMatchDTO(Matches match, TournamentDoc tournamentDoc, String partType) {
        return new ChosenMatchDTO(
                match.getMatchStatus(),
                match.getMatchDate(),
                match.getIp(),
                mapDTOListToFullMatchMapDTOList(match.getMaps()),
                tournamentDoc.getName(),
                "Best of " + match.getMaps().size(),
                tournamentDoc.getType(),
                partType,
                match.getDescription(),
                tournamentDoc.getMapPool(),
                match.getPicks(),
                getFullParticipant(match.getNameFirst(), partType, match.getMatchTeamScore(match.getNameFirst())),
                getFullParticipant(match.getNameSecond(), partType, match.getMatchTeamScore(match.getNameSecond()))
        );
    }


    /**
     * Метод конвертирует список объектов класса {@link MapDTO} в список объектов класса {@link FullMatchMapDTO}
     *
     * @param maps список объектов класса {@link MapDTO}, содержащих полную информацию о картах матча, которые
     *             необходимо конвертировать в список объектов класса {@link FullMatchMapDTO}
     * @return Список объектов класса {@link FullMatchMapDTO}, содержащих информацию о картах матча, необходимых для
     * отображения на frontend
     */
    private ArrayList<FullMatchMapDTO> mapDTOListToFullMatchMapDTOList(ArrayList<MapDTO> maps) {
        ArrayList<FullMatchMapDTO> fullMatchMapDTOS = new ArrayList<>();

        for (MapDTO map : maps) {
            fullMatchMapDTOS.add(new FullMatchMapDTO(
                    map.getMapName(),
                    map.getStatus(),
                    map.getFirstHalf(),
                    map.getSecondHalf(),
                    map.getOvertime(),
                    map.getStats() != null &&
                            map.getStats().getFirstTeam() != null &&
                            map.getStats().getSecondTeam() != null ?
                            new FullMatchStatsDTO(
                                    map.getStats().getFirstTeam().getName(),
                                    getFullPlayerStats(map.getStats().getFirstTeam().getPlayers(), map.getCurrentRound()),
                                    map.getStats().getSecondTeam().getName(),
                                    getFullPlayerStats(map.getStats().getSecondTeam().getPlayers(), map.getCurrentRound())
                            )
                            :
                            null
            ));
        }

        return fullMatchMapDTOS;
    }


    /**
     * Метод позволяет получить статистику об игроках команды и отсортировать их по убыванию KD
     *
     * @param teamPlayers список объектов класса {@link MatchPlayerDTO}, содержащих информацию об игроке на сервере
     * @param curRound    текущий раунд на карте
     * @return Список объектов класса {@link FullMatchPlayerStatsDTO}, отсортированных по убыванию KD, содержащих
     * статистику игроков по прошествии карт(ы)
     */
    private ArrayList<FullMatchPlayerStatsDTO> getFullPlayerStats(ArrayList<MatchPlayerDTO> teamPlayers, int curRound) {
        ArrayList<FullMatchPlayerStatsDTO> teamPlayersStats = new ArrayList<>();

        for (MatchPlayerDTO player : teamPlayers) {
            PlayerDoc playerDoc = playerRepository.findByNick(player.getNick());

            teamPlayersStats.add(
                    new FullMatchPlayerStatsDTO(
                            playerDoc.getCountry(),
                            countryRepository.findByCountryRU(playerDoc.getCountry()).getFlagPathMini(),
                            playerDoc.getFirstName(),
                            playerDoc.getSecondName(),
                            player.getNick(),
                            player.getKills(),
                            player.getDeaths(),
                            player.getAssists(),
                            roundNumber((double) player.getFullDamage() / curRound))
            );
        }

        teamPlayersStats.sort(Comparator.comparingDouble(o -> -getKd(o.getKills(), o.getDeaths())));

        return teamPlayersStats;
    }


    /**
     * Метод позволяет вычислить KD игрока - отношение Kills/Deaths
     *
     * @param kills  количество убийств игрока
     * @param deaths количество смертей игрока
     * @return Количество убийств игрока, если количество смертей равно 0, иначе отношение Kills/Deaths, округленное до
     * двух знаков после запятой
     */
    private double getKd(int kills, int deaths) {
        if (deaths == 0)
            return kills;
        return roundNumber((double) kills / deaths);
    }


    /**
     * Метод позволяет получить полную информацию о стримах, освещающих матч
     *
     * @param streams список объектов класса {@link StreamDTO}, содержащих основную информацию о прямых трансляциях,
     *                освещающих матч
     * @return Список объектов класса {@link StreamFlagDTO}, содержащих информацию о стримах, необходимую для
     * отображения на frontend
     */
    private ArrayList<StreamFlagDTO> getStreams(ArrayList<StreamDTO> streams) {
        ArrayList<StreamFlagDTO> streamFlagList = new ArrayList<>();

        for (StreamDTO stream : streams) {
            String viewers = stream.getLink().contains("twitch.tv") ? getTwitchViewers(stream.getLink()) : getYoutubeViewers(stream.getLink());
            streamFlagList.add(
                    new StreamFlagDTO(stream,
                            countryRepository.findByCountryRU(stream.getCountry()).getFlagPathMini(),
                            viewers)
            );
        }

        return streamFlagList;
    }


    /**
     * Метод позволяет получить информацию об участнике матча, необходимую для отображения состава на странице матча
     *
     * @param partName ник игрока или название команды
     * @param partType тип участников турнира:
     *                 <li><b>player</b> - игрок, если формат турнира 1x1</li>
     *                 <li><b>team</b> - команда, если формат турнира 2x2 или 5x5</li>
     *                 Влияет на формирование возвращаемого значения: например, для команды будет сформирован список
     *                 участников команды
     * @param score    счет команды в матче
     * @return Объект класса {@link FullMatchTeamDTO}, содержащий информацию, необходимую для отображения состава команды
     */
    private FullMatchTeamDTO getFullParticipant(String partName, String partType, int score) {
        if (partType.equals("team")) {
            TeamDoc teamDoc = teamRepository.findByTeamName(partName);
            return new FullMatchTeamDTO(
                    partName,
                    countryRepository.findByCountryRU(teamDoc.getCountry()).getFlagPath(),
                    "",
                    teamDoc.getTop(),
                    score,
                    getPlayers(partName)
            );
        }

        PlayerDoc playerDoc = playerRepository.findByNick(partName);
        return new FullMatchTeamDTO(
                partName,
                countryRepository.findByCountryRU(playerDoc.getCountry()).getFlagPathMini(),
                getTeam(playerRepository.findByNick(partName)),
                -1,
                score,
                new ArrayList<>()
        );
    }


    /**
     * Метод позволяет получить список участников команды, принимающих участие в матче
     *
     * @param teamName название команды, информацию о чьих участниках необходимо получить
     * @return Список объектов класса {@link FlagNameSrcDTO}, содержащих информацию о нике игрока и его стране
     */
    private ArrayList<FlagNameSrcDTO> getPlayers(String teamName) {
//        TODO: ЗДЕСЬ, С ПРИЦЕЛОМ НА БУДУЩЕЕ, СДЕЛАТЬ ПРОВЕРКУ НА isInTeam с датой матча

        List<PlayerDoc> playerDocList = playerRepository.findAll();

        ArrayList<FlagNameSrcDTO> players = new ArrayList<>();

        for (PlayerDoc playerDoc : playerDocList) {
            ArrayList<Rosters> playerRosters = playerDoc.getRosters();

            for (Rosters roster : playerRosters) {
                if (roster.getTeamName().equals(teamName) && roster.getExitDate() == null) {
                    players.add(new FlagNameSrcDTO(playerDoc.getCountry(), playerDoc.getNick(), countryRepository.findByCountryRU(playerDoc.getCountry()).getFlagPathMini()));
                }
            }
        }

        return players;
    }


    /**
     * Метод позволяет получить количество зрителей трансляции на Twitch.<br></br>
     * Подробнее в <a href="https://dev.twitch.tv/docs/api/reference/#get-streams">документации</a>
     *
     * @param twitchLink ссылка на прямую трансляцию на Twitch
     * @return Если при обращении к API Twitch произошла ошибка, возвращается пустая строка, иначе возвращается
     * количество зрителей на трансляции
     */
    private String getTwitchViewers(String twitchLink) {
        String userAgent = RandomUserAgentGenerator.getNextNonMobile();

        RequestBody formBody = new FormBody.Builder()
                .add("client_id", getProperty("client_id_twitch"))
                .add("client_secret", getProperty("client_secret_twitch"))
                .add("grant_type", "client_credentials")
                .build();

        Request tokenRequest = new Request.Builder()
                .url("https://id.twitch.tv/oauth2/token")
                .header("User-Agent", userAgent)
                .post(formBody)
                .build();

        Response tokenResponse;
        try {
            tokenResponse = new OkHttpClient().newCall(tokenRequest).execute();
        } catch (IOException e) {
            return "";
        }

        if (tokenResponse.body() == null)
            return "";

        TwitchCodeResponseDTO codeResponseBody;
        try {
            codeResponseBody = new Gson().fromJson(tokenResponse.body().string(), TwitchCodeResponseDTO.class);
        } catch (IOException e) {
            return "";
        }

        String user_login = twitchLink.substring(twitchLink.lastIndexOf("/") + 1);
        String url = String.format("https://api.twitch.tv/helix/streams?user_login=%s", user_login);

        Request viewersRequest = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Authorization", String.format("Bearer %s", codeResponseBody.getAccess_token()))
                .header("Client-Id", getProperty("client_id_twitch"))
                .build();

        Response viewersResponse;
        try {
            viewersResponse = new OkHttpClient().newCall(viewersRequest).execute();
        } catch (IOException e) {
            return "";
        }

        if (viewersResponse.body() == null)
            return "";

        TwitchViewersResponseDTO viewersResponseBody;
        try {
            viewersResponseBody = new Gson().fromJson(viewersResponse.body().string(), TwitchViewersResponseDTO.class);
        } catch (IOException e) {
            return "";
        }

        if (viewersResponseBody.getData().isEmpty())
            return "";

        return viewersWrapper(viewersResponseBody.getData().get(0).getViewer_count());
    }


    /**
     * Метод позволяет получить количество зрителей трансляции на YouTube.<br></br>
     * Подробнее в <a href="https://developers.google.com/youtube/v3/docs/videos?hl=ru#liveStreamingDetails.concurrentViewers">документации</a>
     *
     * @param youTubeLink ссылка на прямую трансляцию на YouTube
     * @return Если при обращении к API YouTube произошла ошибка, возвращается пустая строка, иначе возвращается
     * количество зрителей на трансляции
     */
    private String getYoutubeViewers(String youTubeLink) {
        String fieldsUrl = "&fields=items%2FliveStreamingDetails%2FconcurrentViewers";
        String url = String.format("https://youtube.googleapis.com/youtube/v3/videos?part=liveStreamingDetails&id=%s%s&key=%s",
                getYoutubeVideoID(youTubeLink), fieldsUrl, getProperty("youtube_api_key"));

        Request viewersRequest = new Request.Builder()
                .url(url)
                .header("User-Agent", RandomUserAgentGenerator.getNextNonMobile())
                .build();

        Response viewersResponse;
        try {
            viewersResponse = new OkHttpClient().newCall(viewersRequest).execute();
        } catch (IOException e) {
            return "";
        }

        if (viewersResponse.body() == null)
            return "";

        YouTubeViewersResponseDTO viewersResponseBody;
        try {
            viewersResponseBody = new Gson().fromJson(viewersResponse.body().string(), YouTubeViewersResponseDTO.class);
        } catch (IOException e) {
            return "";
        }

        ArrayList<YouTubeViewersItemDTO> items = viewersResponseBody.getItems();

        if (items.isEmpty() || items.get(0).getLiveStreamingDetails() == null || items.get(0).getLiveStreamingDetails().getConcurrentViewers() == null)
            return "";

        return viewersWrapper(
                Integer.parseInt(items.get(0).getLiveStreamingDetails().getConcurrentViewers())
        );
    }


    /**
     * Метод позволяет получить ID видео на YouTube
     *
     * @param youTubeLink ссылка на видео на YouTube
     * @return ID видео на YouTube
     */
    private String getYoutubeVideoID(String youTubeLink) {
        return youTubeLink.substring(youTubeLink.indexOf("?v=") + 3);
    }


    /**
     * Метод позволяет отформатировать число зрителей трансляции
     *
     * @param viewersCount число зрителей на трансляции
     * @return Если число зрителей больше или равно 1000 - строка формата ч.ччк, где ч.чч - число зрителей, деленное на
     * 1000 с точностью до двух знаков после запятой. Иначе строковое представление числа зрителей
     */
    private String viewersWrapper(int viewersCount) {
        if (viewersCount < 1000)
            return String.valueOf(viewersCount);

        String strCount = String.format("%.2f", (double) viewersCount / 1000);

        if (strCount.endsWith("0")) {
            int idx = strCount.lastIndexOf("0");
            strCount = strCount.substring(0, idx);
        }
        return strCount.replace(",", ".") + "к";
    }


    /**
     * Метод позволяет округлить значение до двух знаков после запятой
     *
     * @param value значение, которое необходимо округлить
     * @return Округленное значение до двух знаков после запятой
     */
    private double roundNumber(double value) {
        double scale = Math.pow(10, 2);
        return Math.ceil(value * scale) / scale;
    }
}
