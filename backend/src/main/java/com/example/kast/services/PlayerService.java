package com.example.kast.services;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.kast.controllers.dto.other.FlagNameSrcDTO;
import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.controllers.dto.player.*;
import com.example.kast.controllers.dto.player.faceit.FaceitAuthDTO;
import com.example.kast.controllers.dto.player.faceit.FaceitResponseDTO;
import com.example.kast.controllers.dto.tab.*;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.*;
import com.example.kast.mongo_collections.embedded.PlayerFullStats;
import com.example.kast.mongo_collections.embedded.Rosters;
import com.example.kast.mongo_collections.interfaces.*;
import com.google.gson.Gson;
import generator.RandomUserAgentGenerator;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.kast.utils.MatchUtils.getEventWithMatchesByStatus;
import static com.example.kast.utils.MatchUtils.sortMatchesByDate;
import static com.example.kast.utils.PlayerUtils.getTeam;
import static com.example.kast.utils.PlayerUtils.isInTeam;
import static com.example.kast.utils.Utils.getProperty;
import static java.time.temporal.ChronoUnit.YEARS;


/**
 * Данный класс является сервисом, реализующим логику обработки всех запросов, связанных со страницей пользователя
 *
 * @param teamRepository         интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @param playerRepository       интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param adminRepository        интерфейс для взаимодействия с сущностями {@link AdminDoc}
 * @param achievementsTabService объект класса {@link AchievementsTabService} - сервис для получения достижений
 *                               пользователя или команды
 * @param rostersService         объект класса {@link RostersService} - сервис для получения информации о командах
 *                               пользователя
 * @param attendedEventsService  объект класса {@link AttendedEventsService} - сервис, обрабатывающий запросы для
 *                               получения турниров, посещенных пользователем или командой
 * @param countryRepository      интерфейс для взаимодействия с сущностями {@link CountryDoc}
 * @param tournamentRepository   интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @param utilsService           объект класса {@link UtilsService} - сервис, содержащий часто используемые методы без
 *                               привязки к конкретному сервису
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record PlayerService(TeamRepository teamRepository, PlayerRepository playerRepository,
                            AdminRepository adminRepository, AchievementsTabService achievementsTabService,
                            RostersService rostersService, AttendedEventsService attendedEventsService,
                            CountryRepository countryRepository, TournamentRepository tournamentRepository,
                            UtilsService utilsService) {
    /**
     * Метод позволяет получить информацию, необходимую для взаимодействия со страницей пользователя
     *
     * @param player ник пользователя, чья страница просматривается
     * @return Объект класса {@link FullPlayerDTO}, содержащий информацию, необходимую для взаимодействия со страницей
     * игрока на frontend
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    public FullPlayerDTO getFullPlayer(String player) throws AppException {
        if (!playerRepository.existsByNick(player))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        ArrayList<SocialDTO> social = getSocial(player);
        Boolean isAdmin = adminRepository.existsByAdminId(player);

        PlayerDoc playerDoc = playerRepository.findByNick(player);

        NameDTO teamName = new NameDTO(getTeam(playerDoc));

        ArrayList<MatchTabInfoDTO> matchesUpcoming = getPlayerMatches(player, "upcoming");
        ArrayList<MatchTabInfoDTO> matchesEnded = getPlayerMatches(player, "ended");

        ArrayList<EventInfoDTO> lanAchievements = achievementsTabService.getPlayerAchievements(player, "Lan");
        ArrayList<EventInfoDTO> onlineAchievements = achievementsTabService.getPlayerAchievements(player, "Online");

        PlayerFullStats playerFullStats = playerDoc.getStats();
        ArrayList<RosterDTO> playerRosters = rostersService.getPlayerRosters(player);

        ArrayList<EventParticipantsDTO> upcomingEvents = attendedEventsService.getPlayerUpcomingEvents(player);
        ArrayList<EventInfoDTO> endedEvents = attendedEventsService.getPlayerEndedEvents(player);

        FlagNameSrcDTO flagNameDTO = new FlagNameSrcDTO(playerDoc.getCountry(),
                playerDoc.getFirstName() + " " + playerDoc.getSecondName(),
                countryRepository.findByCountryRU(playerDoc.getCountry()).getFlagPathMini());

        String age = getAge(playerDoc.getBdate());

        ArrayList<NameDTO> trophies = getPlayerTrophies(player, lanAchievements, onlineAchievements);

        return new FullPlayerDTO(social, isAdmin, teamName, matchesUpcoming,
                matchesEnded, lanAchievements, onlineAchievements, playerFullStats, playerRosters,
                upcomingEvents, endedEvents, flagNameDTO, age, trophies);
    }


    /**
     * Метод позволяет создать новую команду
     *
     * @param newTeam объект класса {@link NewTeamDTO}, содержащий информацию о новой команде
     * @return Объект класса {@link NewTeamDTO}, содержащий информацию о созданной команде
     * @throws AppException Если команда с таким названием уже существует в базе данных
     */
    public NewTeamDTO createTeam(NewTeamDTO newTeam) throws AppException {
        if (newTeam.getName().equals("NoLogo") || utilsService.isAlreadyExists(newTeam.getTag()) || utilsService.isAlreadyExists(newTeam.getName()))
            throw new AppException("Такая команда уже существует", HttpStatus.BAD_REQUEST);

        teamRepository.save(newTeamDTOToTeamDoc(newTeam));

        PlayerDoc captain = playerRepository.findByNick(newTeam.getCap());

        ArrayList<Rosters> rosters = captain.getRosters();
        rosters.add(new Rosters(LocalDate.now(), null, newTeam.getName()));
        captain.setRosters(rosters);

        playerRepository.save(captain);

        return newTeam;
    }


    /**
     * Метод позволяет сменить или обновить дату рождения пользователя
     *
     * @param newDateDTO объект класса {@link NewDateDTO}, содержащий информацию о новой дате рождения пользователя
     * @return Объект класса {@link NewDateDTO}, содержащий информацию об измененной дате рождения соответствующего
     * пользователя
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    public NewDateDTO changeBDate(NewDateDTO newDateDTO) throws AppException {
        if (!playerRepository.existsByNick(newDateDTO.getPlayer()))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        PlayerDoc playerDoc = playerRepository.findByNick(newDateDTO.getPlayer());

        String[] dateSplit = newDateDTO.getBdate().split("\\.");

        int year = Integer.parseInt(dateSplit[2]);
        int month = Integer.parseInt(dateSplit[1]);
        int day = Integer.parseInt(dateSplit[0]) + 1;

        playerDoc.setBdate(LocalDate.of(year, month, day));
        playerRepository.save(playerDoc);

        return newDateDTO;
    }


    /**
     * Метод позволяет изменить ник пользователя
     *
     * @param newNickDTO объект класса {@link NewNickDTO}, содержащий измененный ник соответствующего игрока
     * @return Объект класса {@link NewNickDTO}, содержащий информацию об измененном нике пользователя
     * @throws AppException Если пользователя со старым ником не существует в базе данных или пользователь с новым
     *                      ником уже существует
     */
    public NewNickDTO changeNick(NewNickDTO newNickDTO) throws AppException {
        String oldNick = newNickDTO.getOldNick();
        String newNick = newNickDTO.getNewNick();

        if (!playerRepository.existsByNick(oldNick))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        if (playerRepository.existsByNick(newNick))
            throw new AppException("Пользователь с таким ником уже существует", HttpStatus.BAD_REQUEST);

        PlayerDoc playerDoc = playerRepository.findByNick(oldNick);
        playerDoc.setOldNick(oldNick);
        playerDoc.setNick(newNick);
        playerRepository.save(playerDoc);

        if (adminRepository.existsByAdminId(oldNick)) {
            adminRepository.deleteByAdminId(oldNick);
            adminRepository.save(new AdminDoc(newNick));
        }

        return newNickDTO;
    }


    /**
     * Метод позволяет установить ссылку на профиль пользователя во ВКонтакте.<br></br>
     * Подробнее в
     * <a href="https://dev.vk.com/ru/api/access-token/authcode-flow-user#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5%20access_token">документации</a>
     *
     * @param player ник игрока, которому необходимо изменить ссылку на профиль во ВКонтакте
     * @param code   временный код, полученный после прохождения авторизации
     * @return Новая ссылка на профиль пользователя во ВКонтакте
     * @throws AppException Если пользователя с таким ником не существует в базе данных; если ссылка на профиль
     *                      ВКонтакте у пользователя уже установлена; если при запросе к API ВКонтакте произошла ошибка;
     *                      если ссылка на привязываемый профиль уже установлена
     * @throws IOException  Если при запросе к API ВКонтакте произошла ошибка
     */
    public String setVKLink(String player, String code) throws AppException, IOException {
        if (!playerRepository.existsByNick(player))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        PlayerDoc playerDoc = playerRepository.findByNick(player);

        if (!playerDoc.getVk().isEmpty())
            throw new AppException("Ссылка на VK уже установлена", HttpStatus.BAD_REQUEST);

        String url = String.format("https://oauth.vk.com/access_token?client_id=%s&client_secret=%s&redirect_uri=%s/social-auth/vk/&code=%s",
                getProperty("client_id_vk"), getProperty("client_secret_vk"), getProperty("client_url"), code);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", RandomUserAgentGenerator.getNextNonMobile())
                .build();

        Response response = client.newCall(request).execute();
        if (response.body() == null)
            throw new AppException("Ошибка при запросе к API VK", HttpStatus.BAD_REQUEST);

        VKResponseDTO responseResult = new Gson().fromJson(response.body().string(), VKResponseDTO.class);

        String userID = responseResult.getUser_id();
        String vkLink = "https://vk.com/id" + userID;

        if (playerRepository.existsByVk(vkLink))
            throw new AppException("Такой VK уже существует, войдите в другой аккаунт", HttpStatus.BAD_REQUEST);

        if (userID != null)
            changeSocial(new ChangeSocialDTO(player, vkLink, "VK"));

        return vkLink;
    }


    /**
     * Метод позволяет установить ссылку на профиль пользователя на Faceit.<br></br>
     * Подробнее в <a href="https://cdn.faceit.com/third_party/docs/FACEIT_Connect_3.0.pdf">документации</a>
     *
     * @param player        ник игрока, которому необходимо изменить ссылку на профиль Faceit
     * @param faceitAuthDTO объект класса {@link FaceitAuthDTO}, содержащий код авторизации и верификатор, полученные с
     *                      frontend
     * @return Новая ссылка на профиль пользователя на Faceit
     * @throws AppException Если пользователя с таким ником не существует в базе данных; если ссылка на профиль
     *                      Faceit у пользователя уже установлена; если при запросе к API Faceit произошла ошибка;
     *                      если ссылка на привязываемый профиль уже установлена
     * @throws IOException  Если при запросе к API Faceit произошла ошибка
     */
    public String setFaceitLink(String player, FaceitAuthDTO faceitAuthDTO) throws AppException, IOException {
        if (!playerRepository.existsByNick(player))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        PlayerDoc playerDoc = playerRepository.findByNick(player);

        if (!playerDoc.getFaceit().isEmpty())
            throw new AppException("Ссылка на Faceit уже установлена", HttpStatus.BAD_REQUEST);

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("code", faceitAuthDTO.getCode())
                .add("grant_type", "authorization_code")
                .add("code_verifier", faceitAuthDTO.getVerifier())
                .build();


        Request request = new Request.Builder()
                .url("https://api.faceit.com/auth/v1/oauth/token")
                .header("User-Agent", RandomUserAgentGenerator.getNextNonMobile())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", getCredentials())
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.body() == null)
            throw new AppException("Ошибка при запросе к API Faceit", HttpStatus.BAD_REQUEST);

        FaceitResponseDTO responseResult = new Gson().fromJson(response.body().string(), FaceitResponseDTO.class);

        if (responseResult.getId_token() == null)
            throw new AppException("Ошибка при запросе к API Faceit", HttpStatus.BAD_REQUEST);

        DecodedJWT jwt = JWT.decode(responseResult.getId_token());
        String nick = jwt.getClaim("nickname").asString();

        String faceitLink = "https://www.faceit.com/en/players/" + nick;

        if (playerRepository.existsByFaceit(faceitLink))
            throw new AppException("Такой Faceit уже существует, войдите в другой аккаунт", HttpStatus.BAD_REQUEST);

        changeSocial(new ChangeSocialDTO(player, faceitLink, "Faceit"));

        return faceitLink;
    }


    /**
     * Метод позволяет получить client_id приложения на Faceit для PKCE аутентификации
     *
     * @return client_id приложения на Faceit
     */
    public String getFaceitClientID() {
        return getProperty("client_id_faceit");
    }


    /**
     * Метод позволяет получить client_id приложения во ВКонтакте для получения ключа доступа пользователя
     *
     * @return client_id приложения во ВКонтакте
     */
    public String getVKClientID() {
        return getProperty("client_id_vk");
    }


    /**
     * Метод позволяет получить client_id приложения в Discord для получения ника пользователя в Discord
     *
     * @return Строка вида <i>STRINGclient_id</i> ввиду того, что client_id Discord в представлении JavaScript не
     * умещается в размер целого числа
     */
    public String getDiscordClientID() {
        return "STRING" + getProperty("client_id_discord");
    }


    /**
     * Метод позволяет установить ссылки на профиль пользователя в Steam и Discord. Также используется для отвязки всех
     * социальных сетей пользователя
     *
     * @param social объект класса {@link ChangeSocialDTO}, содержащий информацию об измененной социальной сети
     *               пользователя
     * @return Объект класса {@link ChangeSocialDTO}, содержащий информацию об измененной социальной сети пользователя
     * @throws AppException Если пользователя с таким ником не существует в базе данных или привязываемый профиль уже
     *                      установлен у одного из пользователей
     */
    public ChangeSocialDTO changeSocial(ChangeSocialDTO social) throws AppException {
        if (!playerRepository.existsByNick(social.getPlayer()))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        if (!social.getLink().isEmpty() &&
                (playerRepository.existsByVk(social.getLink()) ||
                        playerRepository.existsByFaceit(social.getLink()) ||
                        playerRepository.existsByDiscord(social.getLink()) ||
                        playerRepository.existsBySteam(social.getLink())))
            throw new AppException("Данный профиль уже привязан к другому аккаунту", HttpStatus.BAD_REQUEST);

        PlayerDoc playerDoc = playerRepository.findByNick(social.getPlayer());
        switch (social.getSocial()) {
            case "VK" -> playerDoc.setVk(social.getLink());
            case "Steam" -> playerDoc.setSteam(social.getLink());
            case "Discord" -> playerDoc.setDiscord(social.getLink());
            case "Faceit" -> playerDoc.setFaceit(social.getLink());
        }
        playerRepository.save(playerDoc);

        return social;
    }


    /**
     * Метод позволяет получить все матчи пользователя (будущие и завершенные), распределив их по соответствующим турнирам
     *
     * @param player ник пользователя, чьи матчи необходимо получить
     * @param status статус матчей, которые необходимо получить:
     *               <li><b>upcoming</b> - ближайшие матчи</li>
     *               <li><b>ended</b> - завершенные матчи</li>
     * @return Список объектов класса {@link MatchTabInfoDTO}, соответствующих требуемому статуса и содержащих
     * информацию о матчах соответствующего турнира
     */
    public ArrayList<MatchTabInfoDTO> getPlayerMatches(String player, String status) {
        PlayerDoc playerDoc = playerRepository.findByNick(player);

        ArrayList<MatchTabInfoDTO> playerMatchesByEvent = new ArrayList<>();

        List<TournamentDoc> tournamentDocs = tournamentRepository.findAll();

        for (TournamentDoc tournamentDoc : tournamentDocs) {
            LocalDate dateStart = tournamentDoc.getDateStart();
            LocalDate dateEnd = tournamentDoc.getDateEnd();

            ArrayList<Rosters> rosters = playerDoc.getRosters();
            for (Rosters roster : rosters) {
                if (tournamentDoc.isEventParticipant(roster.getTeamName(), playerDoc.getNick()) && isInTeam(dateStart, dateEnd, roster.getEnterDate(), roster.getExitDate())) {
                    MatchTabInfoDTO foundedEvent = getEventWithMatchesByStatus(tournamentDoc, roster.getTeamName(), player, status);

                    if (foundedEvent.getMatches().size() != 0)
                        playerMatchesByEvent.add(foundedEvent);
                }
            }
        }

        if (!playerMatchesByEvent.isEmpty()) {
            for (MatchTabInfoDTO matchesByEvent : playerMatchesByEvent) {
                matchesByEvent.setMatches(sortMatchesByDate(
                        new ArrayList<>(matchesByEvent.getMatches()),
                        status,
                        tournamentRepository.findByName(matchesByEvent.getEvent()))
                );
            }
        }

        return playerMatchesByEvent;
    }


    /**
     * Метод позволяет получить данные для базовой авторизации
     *
     * @return Строка <i>CLIENT_ID:CLIENT_SECRET</i> в кодировке Base64
     */
    private String getCredentials() {
        return Credentials.basic(getProperty("client_id_faceit"), getProperty("client_secret_faceit"));
    }


    /**
     * Метод позволяет получить список социальных сетей пользователя
     *
     * @param player ник пользователя, чей список социальных сетей необходимо получить
     * @return Список объектов класса {@link SocialDTO}, содержащих информацию о социальных сетяз игрока
     */
    private ArrayList<SocialDTO> getSocial(String player) {
        PlayerDoc playerDoc = playerRepository.findByNick(player);

        return new ArrayList<>(List.of(
                new SocialDTO("VK", "white", playerDoc.getVk(), "../../img/social/VK.svg"),
                new SocialDTO("Steam", "white", playerDoc.getSteam(), "../../img/social/Steam.svg"),
                new SocialDTO("Discord", "colored", playerDoc.getDiscord(), "../../img/social/Discord.svg"),
                new SocialDTO("Faceit", "colored", playerDoc.getFaceit(), "../../img/social/Faceit.svg")
        ));
    }


    /**
     * Метод позволяет получить список названий трофеев пользователя
     *
     * @param player             ник игрока, чьи достижения необходимо получить
     * @param lanAchievements    список объектов класса {@link EventInfoDTO}, содержащих информацию о достижениях
     *                           пользователя на Lan
     * @param onlineAchievements список объектов класса {@link EventInfoDTO}, содержащих информацию о достижениях
     *                           пользователя в Online
     * @return Список объектов класса {@link NameDTO}, содержащих названия турниров, где пользователь занял первые
     * места, а также названия личных достижений пользователя
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    private ArrayList<NameDTO> getPlayerTrophies(String player, ArrayList<EventInfoDTO> lanAchievements,
                                                 ArrayList<EventInfoDTO> onlineAchievements) throws AppException {
        if (!playerRepository.existsByNick(player))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        PlayerDoc playerDoc = playerRepository.findByNick(player);
        ArrayList<String> trophies = playerDoc.getTrophies();

        ArrayList<NameDTO> trophiesList = new ArrayList<>();
        for (String trophy : trophies) {
            trophiesList.add(new NameDTO(trophy));
        }

        ArrayList<AttendedEventDTO> attendedEventList = attendedEventsService.getPlayerAttendedEvents(player);

        for (AttendedEventDTO attendedEventDTO : attendedEventList) {
            if (attendedEventDTO.getMvp().equals(player))
                trophiesList.add(new NameDTO(String.format("MVP-%s", attendedEventDTO.getName())));
        }

//        ArrayList<Rosters> rosters = playerDoc.getRosters();
//
//        for (Rosters roster : rosters) {
//            trophiesList.addAll(rostersService.getRosterTrophies(roster));
//        }
        for (EventInfoDTO eventInfoDTO : lanAchievements) {
            if (eventInfoDTO.getPlace().equals("1"))
                trophiesList.add(new NameDTO(eventInfoDTO.getName()));
        }

        for (EventInfoDTO eventInfoDTO : onlineAchievements) {
            if (eventInfoDTO.getPlace().equals("1"))
                trophiesList.add(new NameDTO(eventInfoDTO.getName()));
        }

        return trophiesList;
    }


    /**
     * Метод позволяет определить корректное окончание для отображения возраста пользователя
     *
     * @param bdate дата рождения пользователя, которую необходимо корректно представить в виде строки
     * @return Конкатенация возраста игрока с корректным окончанием. Если <code>bdate</code> был равен <code>null</code>
     * - возвращается "Не указано"
     */
    private String getAge(LocalDate bdate) {

        if (bdate == null)
            return "Не указано";

        long yearsDiff = YEARS.between(bdate, LocalDate.now());

        if (yearsDiff % 100 >= 11 && yearsDiff % 100 <= 14) {
            return yearsDiff + " лет";
        } else if (yearsDiff % 10 == 1) {
            return yearsDiff + " год";
        } else if (yearsDiff % 10 >= 2 && yearsDiff % 100 <= 4) {
            return yearsDiff + " года";
        } else {
            return yearsDiff + " лет";
        }
    }


    /**
     * Метод конвертирует объект класса {@link NewTeamDTO} в объект класса {@link TeamDoc}
     *
     * @param newTeam объект класса {@link NewTeamDTO}, содержащий информацию о созданной команде, который необходимо
     *                конвертировать в объект класса {@link TeamDoc}
     * @return Объект класса {@link TeamDoc}, содержащий информацию о новой команде для сохранения в базу данных
     */
    private TeamDoc newTeamDTOToTeamDoc(NewTeamDTO newTeam) {
        return new TeamDoc(newTeam.getName(), newTeam.getTag(), newTeam.getCap(), "", newTeam.getCountry(),
                newTeam.getCity(), "/teams_logo/NoLogo.svg", -999, -999);
    }
}
