package com.example.kast.utils;


import com.example.kast.controllers.dto.match.MatchPlayerDTO;
import com.example.kast.controllers.dto.match.ScoresDTO;
import com.example.kast.controllers.dto.match.map.LogDTO;
import com.example.kast.controllers.dto.match.map.MapDTO;
import com.example.kast.controllers.dto.match.map.MapStatsDTO;
import com.example.kast.controllers.dto.match.map.MapTeamDTO;
import com.example.kast.enums.LogType;
import com.example.kast.mongo_collections.documents.*;
import com.example.kast.mongo_collections.embedded.Matches;
import com.example.kast.mongo_collections.embedded.PlayerFullStats;
import com.example.kast.mongo_collections.interfaces.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.kast.utils.MatchUtils.getCurrentMapByMatchId;


/**
 * Данный класс реализует логику обработки логов, приходящих с сервера, на котором играется матч
 *
 * @author Кирилл "Tamada" Симовин
 */
public class LogParser {
    /**
     * Список объектов класса {@link MatchPlayerDTO}, содержащий статистику игроков, находящихся на сервере
     */
    private final ArrayList<MatchPlayerDTO> player_stats; // вся статистика

    /**
     * ID матча
     */
    private final int matchId;

    /**
     * Максимальное число раундов, которое возможно сыграть за основное время
     */
    private final int maxRounds = 24;

    /**
     * Интерфейс для взаимодействия с сущностями {@link WeaponDoc}
     */
    private final WeaponRepository weaponRepository;

    /**
     * Интерфейс для взаимодействия с сущностями {@link TournamentDoc}
     */
    private final TournamentRepository tournamentRepository;

    /**
     * Объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого проходит матч
     */
    private final TournamentDoc tournamentDoc;

    /**
     * Интерфейс для взаимодействия с сущностями {@link PlayerDoc}
     */
    private final PlayerRepository playerRepository;

    /**
     * Интерфейс для взаимодействия с сущностями {@link MapPoolDoc}
     */
    private final MapPoolRepository mapPoolRepository;

    /**
     * Интерфейс для взаимодействия с сущностями {@link TeamDoc}
     */
    private final TeamRepository teamRepository;

    /**
     * Объект класса {@link MapDTO}, содержащий информацию о текущей карте матча
     */
    private MapDTO curMap;

    /**
     * Переменная {@link LogType}, содержащая информацию о том, какой(ие) компонент(ы) изменятся после парсинга логов
     */
    private LogType type = LogType.NONE;


    /**
     * Инициализирует и создает новый объект класса LogParser
     *
     * @param event                название турнира, в рамках проходит матч
     * @param matchId              ID матча
     * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
     * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
     * @param mapPoolRepository    интерфейс для взаимодействия с сущностями {@link MapPoolDoc}
     * @param teamRepository       интерфейс для взаимодействия с сущностями {@link TeamDoc}
     * @param weaponRepository     интерфейс для взаимодействия с сущностями {@link WeaponDoc}
     */
    public LogParser(String event, int matchId, TournamentRepository tournamentRepository, PlayerRepository playerRepository,
                     MapPoolRepository mapPoolRepository, TeamRepository teamRepository, WeaponRepository weaponRepository) {
        this.matchId = matchId;
        this.tournamentRepository = tournamentRepository;
        this.tournamentDoc = tournamentRepository.findByName(event);

        this.playerRepository = playerRepository;
        this.mapPoolRepository = mapPoolRepository;
        this.teamRepository = teamRepository;
        this.weaponRepository = weaponRepository;

        this.curMap = getCurrentMapByMatchId(this.tournamentDoc, this.matchId);
        this.player_stats = getPlayerStats();
    }


    /**
     * Метод позволяет парсить входящие логи, и в зависимости от типа лога будет определен компонент(ы), которые
     * изменятся на frontend
     *
     * @return Переменная {@link LogType}, содержащая информацию о том, какой(ие) компонент(ы) изменятся после парсинга
     * логов
     */
    public LogType parse(String readLine) {
        LogDTO logMap = new LogDTO();

        if (readLine.contains("Loading map \"")) {
            String logMapName = getMatchSubstring(" Loading map \\\"(.*?)\\\"", readLine);
            String mapName;
            if (mapPoolRepository.existsByInternalName(logMapName))
                mapName = mapPoolRepository.findByInternalName(logMapName).getName();
            else if (mapPoolRepository.existsByInternalNameWingman(logMapName))
                mapName = mapPoolRepository.findByInternalNameWingman(logMapName).getName();
            else
                mapName = "";

            Matches match = tournamentDoc.getMatchById(matchId);

            ArrayList<MapDTO> matchMaps = match.getMaps();
            for (int i = 0; i < matchMaps.size(); ++i) {
                if (matchMaps.get(i).getStatus().equals("upcoming") && matchMaps.get(i).getMapName().equals(mapName) &&
                        (i == 0 || i == (matchMaps.size() - 1) || matchMaps.get(i - 1).getStatus().equals("ended"))) {
                    curMap = matchMaps.get(i);
                    curMap.setStatus("playing");
                    curMap.setStats(new MapStatsDTO(
                            new MapTeamDTO(match.getNameFirst(), "CT", 0, new ArrayList<>()),
                            new MapTeamDTO(match.getNameSecond(), "T", 0, new ArrayList<>())
                    ));
                    curMap.setCurrentRound(0);
                    type = LogType.MAPNAME;
                }
            }
        }
        if (curMap != null) {
            // Кто-то подключился не сервер
            if ((readLine.contains(" connected") || readLine.contains("entered the game")) && isUser(readLine)) {
                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));

                if (!nick.isEmpty()) {
                    logMap.setType("login");
                    logMap.setNick(nick);
                    type = LogType.LOGS;
                }

                // Кто-то сменил команду
            } else if (readLine.contains("switched from team") && isUser(readLine)) {

                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));
                String team2 = getMatchSubstring("\\\" switched from team <.*?> to <(.*?)>", readLine);

                if (!nick.isEmpty()) {
                    if (!containsPlayer(nick))
                        player_stats.add(new MatchPlayerDTO(nick));

                    setTeam(nick, team2);

                    if (team2.equals("Unassigned")) {
                        player_stats.removeIf(player -> player.getNick().equals(nick));
                    }
                    type = LogType.SCOREBOARD;
                }

                // Кто-то умер от рук другого игрока
            } else if (readLine.contains("killed") && readLine.contains("with")) {
                String gun = getMatchSubstring("] with \"(.*?)\"", readLine);

                String killed_how = getMatchSubstring("] with \".*?\" \\((.*?)\\)", readLine);

                List<String> nicks = getPlayerNicksBySteamUserID(getSteamUserID(readLine));

                if (nicks.size() == 2) {
                    MatchPlayerDTO player1 = findByNickInArray(nicks.get(0));
                    MatchPlayerDTO player2 = findByNickInArray(nicks.get(1));

                    String flashAssisted = getMatchSubstring(">\" (flash-assisted killing) \"", readLine);
                    String flashAssistedSide = "";

                    MatchPlayerDTO flashAssistedPlayer = findByNickInArray(flashAssisted);
                    if (flashAssistedPlayer != null)
                        flashAssistedSide = flashAssistedPlayer.getTeam();

                    String assisted = getMatchSubstring(">\" assisted killing \"", readLine);
                    String assistedSide = "";
                    setAssists(assisted);
                    MatchPlayerDTO assistedPlayer = findByNickInArray(assisted);
                    if (assistedPlayer != null)
                        assistedSide = assistedPlayer.getTeam();

                    if (isTeamKill(nicks.get(0), nicks.get(1)))
                        setStats(nicks.get(0), nicks.get(1), -1);
                    else
                        setStats(nicks.get(0), nicks.get(1), 1);

                    deathReset(nicks.get(1));
                    setKillerGun(nicks.get(0), gun);

                    logMap.setNoscope(false);
                    logMap.setPenetrated(false);
                    logMap.setThroughsmoke(false);
                    logMap.setHeadshot(false);
                    logMap.setAttackerblind(false);

                    String[] killedBy = killed_how.split(" ");

                    for (String kill : killedBy) {
                        switch (kill) {
                            case "noscope" -> logMap.setNoscope(true);
                            case "penetrated" -> logMap.setPenetrated(true);
                            case "throughsmoke" -> logMap.setThroughsmoke(true);
                            case "headshot" -> {
                                logMap.setHeadshot(true);
                                setHsKill(nicks.get(0));
                            }
                            case "attackerblind" -> logMap.setAttackerblind(true);
                        }
                    }

                    if (player1 != null && player2 != null) {
                        logMap.setType("kill");
                        logMap.setNick(nicks.get(0));
                        logMap.setAssisted(assisted);
                        logMap.setFlashAssisted(flashAssisted);
                        logMap.setGun(gun);
                        logMap.setVictim(nicks.get(1));
                        logMap.setVictimSide(player2.getTeam());
                        logMap.setSide(player1.getTeam());
                        logMap.setAssisterSide(assistedSide);
                        logMap.setFlashAssistedSide(flashAssistedSide);

                        type = LogType.SCOREBOARD_LOGS;
                    }
                }

                // Кто-то умер от взрыва бомбы
            } else if (readLine.contains("killed by the bomb")) {
                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));
                MatchPlayerDTO player = findByNickInArray(nick);

                if (player != null) {
                    deathReset(nick);

                    logMap.setType("bombDeath");
                    logMap.setNick(nick);
                    logMap.setSide(player.getTeam());

                    type = LogType.SCOREBOARD_LOGS;
                }

                // Кто-то совершил суицид
            } else if (readLine.contains("committed suicide")) {
                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));
                MatchPlayerDTO player = findByNickInArray(nick);

                if (player != null) {
                    setStats("", nick, 1);
                    deathReset(nick);

                    logMap.setType("suicide");
                    logMap.setNick(nick);
                    logMap.setSide(player.getTeam());

                    type = LogType.SCOREBOARD_LOGS;
                }

                // Начался новый раунд
            } else if (readLine.contains("World triggered \"Round_Start\"")) {
                // Тут equiped ПРОВЕРЯТЬ
                roundStart();
                curMap.setCurrentRound(curMap.getCurrentRound() + 1);

                logMap.setType("roundStarted");

                type = LogType.LOGS;

                // Матч начался
            } else if (readLine.contains("World triggered \"Match_Start\"")) {
                matchStart();

                // Террористы выиграли, взорвав бомбу
            } else if (readLine.contains("triggered \"SFUI_Notice_Target_Bombed\"")) {
                String t_score = getMatchSubstring("\\(T \"(.*?)\"\\)", readLine);
                String ct_score = getMatchSubstring("\\(CT \"(.*?)\"\\)", readLine);

                setTeamsScore(ct_score, t_score);

                logMap.setType("roundEnd");
                logMap.setWinner("T");
                logMap.setScoreCT(Integer.valueOf(ct_score));
                logMap.setScoreT(Integer.valueOf(t_score));
                logMap.setHow("Взорвана бомба");

                type = LogType.SCORE_LOGS_SCOREBOARD;

                // Террористы уничтожили команду спецназа
            } else if (readLine.contains("triggered \"SFUI_Notice_Terrorists_Win\"")) {
                String t_score = getMatchSubstring("\\(T \"(.*?)\"\\)", readLine);
                String ct_score = getMatchSubstring("\\(CT \"(.*?)\"\\)", readLine);

                setTeamsScore(ct_score, t_score);

                logMap.setType("roundEnd");
                logMap.setWinner("T");
                logMap.setScoreCT(Integer.valueOf(ct_score));
                logMap.setScoreT(Integer.valueOf(t_score));
                logMap.setHow("Враги уничтожены");

                type = LogType.SCORE_LOGS_SCOREBOARD;

                // Спецназ уничтожили команду террористов
            } else if (readLine.contains("triggered \"SFUI_Notice_CTs_Win\"")) {
                String t_score = getMatchSubstring("\\(T \"(.*?)\"\\)", readLine);
                String ct_score = getMatchSubstring("\\(CT \"(.*?)\"\\)", readLine);

                setTeamsScore(ct_score, t_score);

                logMap.setType("roundEnd");
                logMap.setWinner("CT");
                logMap.setScoreCT(Integer.valueOf(ct_score));
                logMap.setScoreT(Integer.valueOf(t_score));
                logMap.setHow("Враги уничтожены");

                type = LogType.SCORE_LOGS_SCOREBOARD;

                // Спецназ выиграл по истечению времени
            } else if (readLine.contains("triggered \"SFUI_Notice_Target_Saved\"")) {
                String t_score = getMatchSubstring("\\(T \"(.*?)\"\\)", readLine);
                String ct_score = getMatchSubstring("\\(CT \"(.*?)\"\\)", readLine);

                setTeamsScore(ct_score, t_score);

                logMap.setType("roundEnd");
                logMap.setWinner("CT");
                logMap.setScoreCT(Integer.valueOf(ct_score));
                logMap.setScoreT(Integer.valueOf(t_score));
                logMap.setHow("Время истекло");

                type = LogType.SCORE_LOGS_SCOREBOARD;

                // Спецназ выиграл, обезвредив бомбу
            } else if (readLine.contains("triggered \"SFUI_Notice_Bomb_Defused\"")) {
                String t_score = getMatchSubstring("\\(T \"(.*?)\"\\)", readLine);
                String ct_score = getMatchSubstring("\\(CT \"(.*?)\"\\)", readLine);

                setTeamsScore(ct_score, t_score);

                logMap.setType("roundEnd");
                logMap.setWinner("CT");
                logMap.setScoreCT(Integer.valueOf(ct_score));
                logMap.setScoreT(Integer.valueOf(t_score));
                logMap.setHow("Бомба обезврежена");

                type = LogType.SCORE_LOGS_SCOREBOARD;

                // Игра закончилась
            } else if (readLine.contains("Game Over:") && readLine.contains(" score ")) {
                onMapEnded();
                type = LogType.SCORE_SCOREBOARD;

                // Команда, играющая за спецназ
            } else if (readLine.contains("Team playing \"CT\":")) {
                String new_ct = getMatchSubstring(": Team playing \"CT\": (.*)", readLine);

                if (isTeamsSwapped("CT", new_ct)) {
                    swapTeams();
                    type = LogType.SCOREBOARD;
                }

                // Команда, играющая за террористов
            } else if (readLine.contains("Team playing \"TERRORIST\":")) {
                String new_t = getMatchSubstring(": Team playing \"TERRORIST\": (.*)", readLine);

                if (isTeamsSwapped("T", new_t)) {
                    swapTeams();
                    type = LogType.SCOREBOARD;
                }

                // Кто-то совершил покупку
            } else if (readLine.contains("purchased")) {
                String purchase = getMatchSubstring(">\" purchased \"(.*?)\"", readLine);
                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));
                checkPurchase(nick, purchase);
                type = LogType.SCOREBOARD;

                // Кто-то выкинул снаряжение
            } else if (readLine.contains("\" dropped \"")) {
                String droppedGun = getMatchSubstring(">\" dropped \"(.*?)\"", readLine);

                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));

                setDroppedGun(nick, droppedGun);
                type = LogType.SCOREBOARD;

                // Кто-то поднял снаряжение
            } else if (readLine.contains(">\" picked up \"")) {
                String pickedGun = getMatchSubstring(">\" picked up \"(.*?)\"", readLine);

                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));

                if (pickedGun.equals("hkp2000"))
                    pickedGun = "usp_silencer";

                setPickedEquip(nick, pickedGun);
                type = LogType.SCOREBOARD;

                // Количество денег игрока изменилось
            } else if (readLine.contains("money change")) {

                String cur_money = getMatchSubstring(">\\\" money change [0-9]\\d*[-+][0-9]\\d* = \\$([0-9]\\d*) ", readLine);
                int money = Integer.parseInt(cur_money);

                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));

                setMoney(nick, money);
                type = LogType.SCOREBOARD;

                // Кто-то попал в кого-то
            } else if (readLine.contains("hitgroup \"") && readLine.contains("attacked")) {
                int hp = Integer.parseInt(getMatchSubstring(" \\(health \"(.*?)\"\\)", readLine));
                List<String> nicks = getPlayerNicksBySteamUserID(getSteamUserID(readLine));

                if (nicks.size() == 2) {
                    if (hp >= 1) {
                        int armor = Integer.parseInt(getMatchSubstring("\\) \\(armor \"(.*?)\"\\) \\(", readLine));

                        if (armor == 0)
                            resetArmor(nicks.get(1));

                        setHp(nicks.get(1), hp);
                    }

                    int damage = Integer.parseInt(getMatchSubstring("\\\" \\(damage \\\"(.*?)\\\"\\) \\(", readLine));
                    setDamage(nicks.get(0), nicks.get(1), damage);
                    type = LogType.SCOREBOARD;
                }

                // Кто-то поставил бомбу
            } else if (readLine.contains("triggered \"Planted_The_Bomb\" at")) {
                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));
                String bombsite = getMatchSubstring("\\\" at bombsite (\\w)", readLine);
                ArrayList<Integer> situation = getSituation();

                logMap.setType("bombPlanted");
                logMap.setNick(nick);
                logMap.setPlant(bombsite);
                logMap.setTAlive(situation.get(0));
                logMap.setCtAlive(situation.get(1));
                type = LogType.LOGS;

                // Кто-то разминировал бомбу
            } else if (readLine.contains("triggered \"Defused_The_Bomb\"")) {
                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));

                logMap.setType("bombDefused");
                logMap.setNick(nick);
                type = LogType.LOGS;

                // Кто-то покинул сервер
            } else if (readLine.contains("disconnected (reason \"")) {
                String nick = getPlayerNickBySteamUserID(getSteamUserID(readLine));

                MatchPlayerDTO curPlayer = findByNickInArray(nick);

                if (curPlayer != null) {
                    player_stats.removeIf(player -> player.getNick().equals(nick));

                    logMap.setType("logout");
                    logMap.setNick(nick);
                    logMap.setSide(curPlayer.getTeam());
                    type = LogType.SCOREBOARD_LOGS;
                }
            }
            setStatsAndLogs(logMap);
        }
        return type;
    }


    /**
     * Метод позволяет получить подстроку, соответствующую переданному регулярному выражению
     *
     * @param regex регулярное выражение, с использованием которого необходимо получить подстроку
     * @param input строка, из которой необходимо получить подстроку
     * @return Если в строке присутствует подстрока, соответствующая регулярному выражению - возвращается подстрока.
     * Иначе - пустая строка
     */
    public String getMatchSubstring(String regex, String input) {
        Matcher matcher = Pattern.compile(regex).matcher(input);

        if (!matcher.find()) {
            return "";
        }

        return matcher.group(1);
    }


//    private MapDTO getCurMap() {
//        Matches match = tournamentDoc.getMatchById(matchId);
//
//        if (match == null)
//            return null;
//
//        ArrayList<MapDTO> matchMaps = match.getMaps();
//        for (MapDTO map : matchMaps) {
//            if (map.getStatus().equals("playing")) {
//                return map;
//            }
//        }
//
//        return null;
//    }


    /**
     * Метод позволяет получить статистику игроков, находящихся на карте
     *
     * @return Список объектов класса {@link MatchPlayerDTO}, содержащий статистику игроков, находящихся на сервере
     */
    private ArrayList<MatchPlayerDTO> getPlayerStats() {
        ArrayList<MatchPlayerDTO> playerStats = new ArrayList<>();

        if (curMap == null)
            return playerStats;

        if (curMap.getStats() != null) {
            if (curMap.getStats().getFirstTeam() != null && curMap.getStats().getFirstTeam().getPlayers() != null)
                playerStats.addAll(curMap.getStats().getFirstTeam().getPlayers());

            if (curMap.getStats().getSecondTeam() != null && curMap.getStats().getSecondTeam().getPlayers() != null)
                playerStats.addAll(curMap.getStats().getSecondTeam().getPlayers());
        }
        return playerStats;
    }


    /**
     * Метод позволяет найти объект класса {@link MatchPlayerDTO}, содержащий информацию об игроке с запрашиваемом ником
     *
     * @param nick ник игрока, о котором необходимо получить информацию
     * @return Если игрок с запрашиваемым ником не был найден - <code>null</code>. Иначе объект класса
     * {@link MatchPlayerDTO}, содержащий информацию об игроке
     */
    private MatchPlayerDTO findByNickInArray(String nick) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick)) {
                return player;
            }
        }
        return null;
    }


    /**
     * Метод позволяет вычленить из строки SteamID2 игроков
     *
     * @param inputStr строка, из которой необходимо получить SteamID2 игроков
     * @return Список, содержащий SteamID2 игроков
     * @see #getSteamUserID(String)
     * @deprecated С момента выхода CS2, где SteamID2 заменили на SteamUserID
     */
    @Deprecated
    private List<String> getSteamID2(String inputStr) {
        String regex = "<(STEAM_\\d:[0-1]:\\d+)>";

        Matcher matcher = Pattern.compile(regex).matcher(inputStr);

        List<String> steamIDs = new ArrayList<>();

        while (matcher.find()) {
            steamIDs.add(matcher.group(1));
        }

        return steamIDs;
    }


    /**
     * Метод позволяет определить, содержит ли строка упоминание SteamUserID. <br></br>
     * Используется для определения того, совершил действие пользователь или бот
     *
     * @param inputLine строка, в которой необходимо проверить наличие SteamUserID
     * @return Содержит ли строка упоминание SteamUserID: <code>true</code>, если содержит; <code>false</code> иначе
     */
    private boolean isUser(String inputLine) {
        return Pattern.compile("<\\[U:[0-1]:\\d+\\]>").matcher(inputLine).find();
    }


    /**
     * Метод позволяет вычленить из строки SteamUserID игроков
     *
     * @param inputStr строка, из которой необходимо получить SteamUserID игроков
     * @return Список, содержащий SteamUserID игроков
     */
    private List<String> getSteamUserID(String inputStr) {
        String regex = "<\\[(U:[0-1]:\\d+)\\]>";

        Matcher matcher = Pattern.compile(regex).matcher(inputStr);

        List<String> steamIDs = new ArrayList<>();

        while (matcher.find()) {
            steamIDs.add(matcher.group(1));
        }

        return steamIDs;
    }


    /**
     * Метод позволяет получить по SteamUserID ники игроков, под которыми они регистрировались на сайте
     *
     * @param steamUserIDList список SteamUserID игроков, для которых необходимо получить ники из базы данных
     * @return Список ников игроков из базы данных, соответствующих запрашиваемым SteamUserID
     */
    private List<String> getPlayerNicksBySteamUserID(List<String> steamUserIDList) {
        List<String> playersNicks = new ArrayList<>();
        List<PlayerDoc> playerDocList = playerRepository.findAll();

        for (String steamUserID : steamUserIDList) {
            long steamID64 = steamUserIDTo64(steamUserID);

            if (steamID64 == 0)
                continue;

            for (PlayerDoc playerDoc : playerDocList) {
                if (!playerDoc.getSteam().isEmpty() && playerDoc.getSteam().contains(String.valueOf(steamID64))) {
                    playersNicks.add(playerDoc.getNick());
                    break;
                }
            }
        }
        return playersNicks;
    }


    /**
     * Метод позволяет получить по SteamUserID ник игрока, под которым он регистрировался на сайте
     *
     * @param steamUserIDList список из одного элемента - SteamUserID игрока, для которого необходимо получить ник из
     *                        базы данных
     * @return Если игрок был найден - ник игрока в базе данных, соответствующий запрашиваемому SteamUserID. Иначе -
     * пустая строка
     */
    private String getPlayerNickBySteamUserID(List<String> steamUserIDList) {
        List<String> result = getPlayerNicksBySteamUserID(steamUserIDList);
        if (result.isEmpty())
            return "";

        return result.get(0);
    }


    /**
     * Метод позволяет конвертировать SteamID2 игрока в соответствующий ему SteamID64
     *
     * @param steamID2 SteamID2, который необходимо конвертировать в SteamID64
     * @return Если передан валидный SteamID2 - возвращается соответствующий ему SteamID64. Иначе - 0
     * @see #steamUserIDTo64(String)
     * @deprecated С момента выхода CS2, где SteamID2 заменили на SteamUserID
     */
    @Deprecated
    private long steamID2To64(String steamID2) {
        String regex = "^STEAM_\\d:[0-1]:\\d+$";
        Matcher matcher = Pattern.compile(regex).matcher(steamID2);
        if (!matcher.matches())
            return 0;

        String[] split = steamID2.split(":");

        long v = 76561197960265728L;
        long y = Long.parseLong(split[1]);
        long z = Long.parseLong(split[2]);

        return (z * 2) + v + y;
    }


    /**
     * Метод позволяет конвертировать SteamUserID игрока в соответствующий ему SteamID64
     *
     * @param steamUserID SteamUserID, который необходимо конвертировать в SteamID64
     * @return Если передан валидный SteamUserID - возвращается соответствующий ему SteamID64. Иначе - 0
     */
    private long steamUserIDTo64(String steamUserID) {
        String regex = "^U:[0-1]:\\d+$";
        Matcher matcher = Pattern.compile(regex).matcher(steamUserID);
        if (!matcher.matches())
            return 0;

        String[] split = steamUserID.split(":");

        long steamID64Indent = 76561197960265728L;

        return steamID64Indent + Long.parseLong(split[2]);
    }


    /**
     * Метод позволяет определить поменялись ли команды сторонами
     *
     * @param side     сторона (CT или T), для которой необходимо проверить, сменились ли команды
     * @param teamTag тег команды, которая, потенциально, сменила сторону
     * @return Поменялись ли команды сторонами: <code>true</code>, если да; <code>false</code> иначе
     */
    private boolean isTeamsSwapped(String side, String teamTag) {
        MapTeamDTO firstTeam = curMap.getStats().getFirstTeam();
        if (firstTeam.getSide().equals(side)) {
            return !teamRepository.findByTeamName(firstTeam.getName()).getTag().equals(teamTag);
        }
        return !teamRepository.findByTeamName(curMap.getStats().getSecondTeam().getName()).getTag().equals(teamTag);
    }


    /**
     * Метод позволяет изменить статистику игроков и обновить список логов после парсинга входящей строки
     *
     * @param logMap объект класса {@link LogDTO}, содержащий информацию о распаршеной строке
     */
    private void setStatsAndLogs(LogDTO logMap) {
        ArrayList<LogDTO> logs = curMap.getLogs();
        if (logMap.getType() != null && !logMap.getType().equals("null")) {
            if (logs == null)
                logs = new ArrayList<>();
            logs.add(0, logMap);
        }

        ArrayList<MatchPlayerDTO> playersCT = new ArrayList<>();
        ArrayList<MatchPlayerDTO> playersT = new ArrayList<>();

        for (MatchPlayerDTO player : player_stats) {
            if (player.getTeam().equals("CT"))
                playersCT.add(player);
            else if (player.getTeam().equals("TERRORIST"))
                playersT.add(player);
        }

        Matches match = tournamentDoc.getMatchById(matchId);

        ArrayList<MapDTO> matchMaps = match.getMaps();
        for (MapDTO map : matchMaps) {
            if (!map.getMapName().equals(curMap.getMapName()))
                continue;

            String sideFirst = curMap.getStats().getFirstTeam().getSide();

            if (sideFirst.equals("CT")) {
                curMap.getStats().getFirstTeam().setPlayers(playersCT);
                curMap.getStats().getSecondTeam().setPlayers(playersT);
            } else {
                curMap.getStats().getFirstTeam().setPlayers(playersT);
                curMap.getStats().getSecondTeam().setPlayers(playersCT);
            }
            map.setStats(curMap.getStats());
            map.setLogs(logs);

            match.setMaps(matchMaps);
            break;
        }

        tournamentDoc.editMatchList(match);
        tournamentRepository.save(tournamentDoc);
    }


    /**
     * Метод вызывается в конце карты и позволяет обновить:
     * <li>Статистику каждого игрока</li>
     * <li>Информацию по половинам и овертаймам</li>
     * <li>Статус карты</li>
     * <li>Дату окончания матча, если карта оказалась финальной</li>
     */
    private void onMapEnded() {
        if (curMap.getSecondHalf() == null) {
            curMap.setSecondHalf(new ScoresDTO(
                    Math.abs(curMap.getStats().getSecondTeam().getScore() - curMap.getFirstHalf().getScoreFirst()),
                    curMap.getStats().getSecondTeam().getSide(),
                    Math.abs(curMap.getStats().getFirstTeam().getScore() - curMap.getFirstHalf().getScoreSecond()),
                    curMap.getStats().getFirstTeam().getSide()
            ));
        }

        Matches match = tournamentDoc.getMatchById(matchId);

        ArrayList<MapDTO> matchMaps = match.getMaps();
        for (MapDTO map : matchMaps) {
            if (!map.getMapName().equals(curMap.getMapName()))
                continue;

            if (curMap.getCurrentRound() > maxRounds) {
                setOvertimes(match.getNameFirst());
            }
            map.setOvertime(curMap.getOvertime());
            map.setStatus("ended");
            break;
        }

        if (match.isSomebodyWon())
            match.setMatchEndDate(LocalDateTime.now());

        for (MatchPlayerDTO player : player_stats) {
            PlayerDoc playerDoc = playerRepository.findByNick(player.getNick());

            PlayerFullStats playerStats = playerDoc.getStats();
            playerStats.setKills(playerStats.getKills() + player.getKills());
            playerStats.setHsKills(playerStats.getHsKills() + player.getHsKills());
            playerStats.setMaps(playerStats.getMaps() + 1);
            playerStats.setRoundsPlayed(playerStats.getRoundsPlayed() + curMap.getCurrentRound());
            playerStats.setFullDamage(playerStats.getFullDamage() + player.getFullDamage());
            playerStats.setDeaths(playerStats.getDeaths() + player.getDeaths());

            playerDoc.setStats(playerStats);

            playerRepository.save(playerDoc);
        }

        tournamentDoc.editMatchList(match);
        tournamentRepository.save(tournamentDoc);
    }


    /**
     * В данном методе мы проверяем команды двух игроков (убийцы и убитого) на идентичность,
     * чтобы выяснить, тимкилл ли это
     *
     * @param killerNick - ник убийцы
     * @param victimNick - ник жертвы
     * @return Является ли убийство тимкилом: <code>true</code>, если является; <code>false</code> иначе
     */
    private boolean isTeamKill(String killerNick, String victimNick) {
        String killerTeam = "";
        String victimTeam = "";

        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(killerNick))
                killerTeam = player.getTeam();
            else if (player.getNick().equals(victimNick))
                victimTeam = player.getTeam();
        }

        return killerTeam.equals(victimTeam) && !killerTeam.isEmpty();
    }


    /**
     * Метод позволяет обновить счет команд на карте после завершенного раунда
     *
     * @param ctScore счет спецназа после завершения раунда
     * @param tScore  счет террористов после завершения раунда
     */
    private void setTeamsScore(String ctScore, String tScore) {
        if (curMap.getStats().getFirstTeam().getSide().equals("CT")) {
            curMap.getStats().getFirstTeam().setScore(Integer.parseInt(ctScore));
            curMap.getStats().getSecondTeam().setScore(Integer.parseInt(tScore));
        } else {
            curMap.getStats().getFirstTeam().setScore(Integer.parseInt(tScore));
            curMap.getStats().getSecondTeam().setScore(Integer.parseInt(ctScore));
        }

        int score = Integer.parseInt(ctScore) + Integer.parseInt(tScore);
        if (score == (maxRounds / 2)) {
            curMap.setFirstHalf(new ScoresDTO(
                    curMap.getStats().getFirstTeam().getScore(),
                    curMap.getStats().getFirstTeam().getSide(),
                    curMap.getStats().getSecondTeam().getScore(),
                    curMap.getStats().getSecondTeam().getSide()
            ));
        } else if (score == maxRounds) {
            curMap.setSecondHalf(new ScoresDTO(
                    Math.abs(curMap.getStats().getSecondTeam().getScore() - curMap.getFirstHalf().getScoreFirst()),
                    curMap.getStats().getSecondTeam().getSide(),
                    Math.abs(curMap.getStats().getFirstTeam().getScore() - curMap.getFirstHalf().getScoreSecond()),
                    curMap.getStats().getFirstTeam().getSide()
            ));
        } else if (score > maxRounds && curMap.getOvertime() == null) {
            curMap.setOvertime(new ScoresDTO(
                    null,
                    "",
                    null,
                    ""
            ));
        }
    }


    /**
     * В данном методе мы получаем ситуацию (количество КТ и Т) на момент поставленной бомбы
     *
     * @return Список целых чисел, содержащий два элемента - информацию о количестве живых игроков террористов и спецназа
     * соответственно
     */
    private ArrayList<Integer> getSituation() {
        int ct = 0;
        int t = 0;
        for (MatchPlayerDTO player : player_stats) {
            if (player.getHp() > 0) {
                if (player.getTeam().equals("CT"))
                    ct++;
                else if (player.getTeam().equals("TERRORIST"))
                    t++;
            }
        }
        ArrayList<Integer> situation = new ArrayList<>();
        situation.add(t);
        situation.add(ct);
        return situation;
    }


    /**
     * Данный метод изменяет количество денег соответствующего игрока
     *
     * @param nick       ник игрока, у которого нужно изменить количество денег
     * @param moneyToSet измененное количество денег игрока
     */
    private void setMoney(String nick, int moneyToSet) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick)) {
                player.setMoney(moneyToSet);
                break;
            }
        }
    }


    /**
     * Данный метод изменяет количество хп соответствующего игрока
     *
     * @param nick ник игрока, у которого нужно изменить количество хп
     * @param hp   измененное количество хп игрока
     */
    private void setHp(String nick, int hp) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick)) {
                player.setHp(hp);
                break;
            }
        }
    }


    /**
     * Данный метод увеличивает количество убийств в голову у соответствующего игрока
     *
     * @param nick ник игрока, у которого нужно увеличить количество убийств в голову
     */
    private void setHsKill(String nick) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick)) {
                player.setHsKills(player.getHsKills() + 1);
                break;
            }
        }
    }


    /**
     * Данный метод увеличивает общий урон, нанесенный соответствующим игроком
     *
     * @param damageDealerNick ник игрока, у которого нужно увеличить общий урон - человек, нанесший урон
     * @param damagedNick      ник игрока, которому нанесли урон
     * @param damage           количество нанесенного урона
     */
    private void setDamage(String damageDealerNick, String damagedNick, int damage) {
        MatchPlayerDTO damagedPlayer = findByNickInArray(damagedNick);
        if (damagedPlayer == null)
            return;

        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(damageDealerNick) && !damageDealerNick.equals(damagedNick) && !damagedPlayer.getTeam().equals(player.getTeam())) {
                player.setFullDamage(player.getFullDamage() + damage);
            }
        }
    }


    /**
     * Данный метод убирает броню у соответствующего игрока
     *
     * @param nick ник игрока, у которого нужно убрать броню
     */
    private void resetArmor(String nick) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick)) {
                player.setArmor(0);
                break;
            }
        }
    }


    /**
     * В данном методе, в зависимости от совершённой покупки, ставится то или иное значение
     * объекта класса {@link MatchPlayerDTO}
     *
     * @param nick     ник игрока, совершившего покупку
     * @param purchase экипировка, купленная игроком
     */
    private void checkPurchase(String nick, String purchase) {
        for (MatchPlayerDTO player : player_stats) {
            if (!player.getNick().equals(nick))
                continue;

            switch (purchase) {
                case "item_defuser" -> player.setDefuseKit(true);
                case "item_kevlar" -> player.setArmor(1);
                case "item_assaultsuit" -> player.setArmor(2);
                default -> {
                    if (weaponRepository.existsByName(purchase) && weaponRepository.findByName(purchase).getType().equals("pistol"))
                        player.setPistol(purchase);
                    player.setWeapon(getBestGun(player.getWeapon(), purchase));
                }
            }

            break;
        }
    }


    /**
     * В данном методе увеличивается количество ассистов соответствующего игрока
     *
     * @param nick ник игрока, совершившего ассист
     */
    private void setAssists(String nick) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick)) {
                int assists = player.getAssists() + 1;
                player.setAssists(assists);
                break;
            }
        }
    }


    /**
     * Метод вызывается при старте матча и задает каждому полю игрока настройки, соответствующие соревновательным правилам
     */
    private void matchStart() {
        for (MatchPlayerDTO player : player_stats) {
            player.setWeapon("");
            player.setPistol("");
            player.setDefuseKit(false);
            player.setHp(100);
            player.setArmor(0);
            player.setMoney(800);
            player.setKills(0);
            player.setDeaths(0);
            player.setAssists(0);
        }
    }


    /**
     * При старте раунда каждому игроку восстанавливают здоровье и дают стартовое оружие, если таковое отсутствует
     */
    private void roundStart() {
        for (MatchPlayerDTO player : player_stats) {
            player.setHp(100);
            if (player.getWeapon().isEmpty()) {
                if (player.getTeam().equals("CT")) {
                    player.setWeapon("usp_silencer");
                    player.setPistol("usp_silencer");
                } else if (player.getTeam().equals("TERRORIST")) {
                    player.setWeapon("glock");
                    player.setPistol("glock");
                }
            }
        }
    }


    /**
     * Данный метод обновляет статистику убийцы и убитого - увеличивает количество убийств и смертей у соответствующих
     * игроков
     *
     * @param killerNick ник убийцы. У него увеличивается количество убийств
     * @param victimNick ник убитого. У него увеличивается количество смертей
     */
    private void setStats(String killerNick, String victimNick, int score) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(killerNick)) {
                player.setKills(player.getKills() + score);
            }

            if (player.getNick().equals(victimNick)) {
                player.setDeaths(player.getDeaths() + 1);
            }
        }
    }


    /**
     * Данный метод обнуляет все показатели игрока при смерти
     *
     * @param nick ник умершего игрока, у которого обнуляются все характеристики
     */
    private void deathReset(String nick) {
        //Если чел умер - не показывается армор, дефуза, оружие, а хп на 0
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick)) {
                player.setArmor(0);
                player.setDefuseKit(false);
                player.setHp(0);
                player.setWeapon("");

                break;
            }
        }
    }


    /**
     * Данный метод заменяет текущее оружие на то, с которого только что игрок совершил убийство
     *
     * @param nick ник игрока, совершившего убийство
     * @param gun  название оружия, с которого игрок совершил убийство
     */
    private void setKillerGun(String nick, String gun) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick)) {
                player.setWeapon(getBestGun(player.getWeapon(), gun));
                break;
            }
        }
    }


    /**
     * Данный метод удаляет у соответствующего игрока выброшенное оружие
     *
     * @param nick ник игрока, выбросившего оружие
     * @param gun  название выброшенного оружия
     */
    private void setDroppedGun(String nick, String gun) {
        if (!weaponRepository.existsByName(gun))
            return;

        for (MatchPlayerDTO player : player_stats) {
            if (!player.getNick().equals(nick))
                continue;

            String playerPistol = player.getPistol();

            if (playerPistol.equals(gun) || playerPistol.equals("cz75a") && gun.equals("p250") || playerPistol.equals("usp_silencer") && gun.equals("hkp2000"))
                player.setPistol("");

            if (player.getWeapon().equals(gun))
                player.setWeapon(playerPistol);

            break;
        }
    }


    /**
     * Данный метод устанавливает поднятое снаряжение соответствующему игроку
     *
     * @param nick  ник игрока, поднявшего оружие
     * @param equip название поднятого снаряжения
     */
    private void setPickedEquip(String nick, String equip) {
        if (!weaponRepository.existsByName(equip) && !equip.equals("defuser"))
            return;

        for (MatchPlayerDTO player : player_stats) {
            if (!player.getNick().equals(nick))
                continue;

            String currentGun = player.getWeapon();

            if (weaponRepository.findByName(equip).getType().equals("pistol"))
                player.setPistol(equip);

            if (!equip.equals("defuser")) {
//                if (currentGun.isEmpty())
//                    player.setWeapon(equip);

                player.setWeapon(getBestGun(currentGun, equip));
            } else {
                player.setDefuseKit(true);
            }

            break;
        }
    }


    /**
     * В данном методе происходит выбор оружия для отображения. Отображается "лучшее" оружие
     *
     * @param curGun нынешнее оружие игрока
     * @param newGun оружие, которое игрок получил каким-либо образом. С ним происходит сравнение
     * @return Оружие, которое будет отображено
     */
    private String getBestGun(String curGun, String newGun) {
        if (weaponRepository.existsByName(newGun)) {
            if (!weaponRepository.existsByName(curGun))
                return newGun;

            String curGunType = weaponRepository.findByName(curGun).getType();
            String newGunType = weaponRepository.findByName(newGun).getType();

            // Пистолет и пистолет
            if (curGunType.equals("pistol") && newGunType.equals("pistol"))
                return newGun;

            // Пистолет и всё остальное
            if (curGunType.equals("pistol") && newGunType.equals("other"))
                return newGun;

            // Всё остальное и всё остальное
            if (curGunType.equals("other") && newGunType.equals("other"))
                return newGun;
        }
        return curGun;
    }


    /**
     * В данном методе соответствующему игроку изменяется команда при смене сторон
     *
     * @param nick ник игрока, которому необходимо изменить команду
     * @param team новая команда (CT или T) игрока
     */
    private void setTeam(String nick, String team) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick)) {
                player.setTeam(team);
                break;
            }
        }
    }


    /**
     * В данном методе проверяется наличие игрока с запрашиваемым ником в списке игроков
     *
     * @param nick ник игрока, наличие которого необходимо проверить
     * @return Находится ли игрок с запрашиваемым ником в списке игроков: <code>true</code>, если да; <code>false</code>
     * иначе
     */
    private boolean containsPlayer(String nick) {
        for (MatchPlayerDTO player : player_stats) {
            if (player.getNick().equals(nick))
                return true;
        }
        return false;
    }


    /**
     * Метод позволяет поменять команды сторонами. Если команды меняются сторонами на овертаймах, то дополнительно
     * вызывается метод для изменения статистики по овертаймам
     *
     * @see #setOvertimes(String)
     */
    private void swapTeams() {
        if (curMap.getCurrentRound() > maxRounds)
            setOvertimes(tournamentDoc.getMatchById(matchId).getNameFirst());

        if (curMap.getStats().getFirstTeam().getSide().equals("CT")) {
            curMap.getStats().getFirstTeam().setSide("T");
            curMap.getStats().getSecondTeam().setSide("CT");
        } else {
            curMap.getStats().getFirstTeam().setSide("CT");
            curMap.getStats().getSecondTeam().setSide("T");
        }

        MapTeamDTO temp = curMap.getStats().getSecondTeam();
        curMap.getStats().setSecondTeam(curMap.getStats().getFirstTeam());
        curMap.getStats().setFirstTeam(temp);
    }


    /**
     * Метод позволяет установить счет каждой команды за соответствующие стороны во время овертаймов
     *
     * @param firstTeamName название первой команды. Используется для опредления сторон команд с целью корректного изменения
     *                      счета
     */
    private void setOvertimes(String firstTeamName) {
        int halfRounds = maxRounds / 2;

        int scoreFirst = curMap.getStats().getFirstTeam().getScore() - halfRounds;
        int scoreSecond = curMap.getStats().getSecondTeam().getScore() - halfRounds;

        String sideFirst = curMap.getStats().getFirstTeam().getSide();
        String sideSecond = curMap.getStats().getSecondTeam().getSide();

        if (curMap.getStats().getFirstTeam().getName().equals(firstTeamName)) {
            if (curMap.getOvertime() != null) {
                curMap.getOvertime().setScoreFirst(scoreFirst);
                curMap.getOvertime().setScoreSecond(scoreSecond);
            } else
                curMap.setOvertime(new ScoresDTO(scoreFirst, sideFirst, scoreSecond, sideSecond));
        } else {
            if (curMap.getOvertime() != null) {
                curMap.getOvertime().setScoreFirst(scoreSecond);
                curMap.getOvertime().setScoreSecond(scoreFirst);
            } else
                curMap.setOvertime(new ScoresDTO(scoreSecond, sideSecond, scoreFirst, sideFirst));
        }
    }
}
