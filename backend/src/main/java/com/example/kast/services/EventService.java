package com.example.kast.services;


import com.example.kast.controllers.dto.event.*;
import com.example.kast.controllers.dto.matches.MatchDTO;
import com.example.kast.controllers.dto.matches.MatchTimeByDateDTO;
import com.example.kast.controllers.dto.other.FlagNameSrcDTO;
import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.*;
import com.example.kast.mongo_collections.embedded.PrizePlaces;
import com.example.kast.mongo_collections.embedded.Requests;
import com.example.kast.mongo_collections.embedded.Rosters;
import com.example.kast.mongo_collections.interfaces.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.kast.utils.EventUtils.getMapPoolArray;
import static com.example.kast.utils.EventUtils.parseEventDate;
import static com.example.kast.utils.MatchUtils.getAttendedEndedMatches;
import static com.example.kast.utils.MatchUtils.sortMatchesByDateTime;
import static com.example.kast.utils.PlayerUtils.getTeam;
import static com.example.kast.utils.Utils.replaceDashes;


/**
 * Данный класс является сервисом, реализующим логику для обработки всех запросов, связанных с турниром
 *
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @param teamRepository       интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @param countryRepository    интерфейс для взаимодействия с сущностями {@link CountryDoc}
 * @param mapPoolRepository    интерфейс для взаимодействия с сущностями {@link MapPoolDoc}
 * @param adminRepository      интерфейс для взаимодействия с сущностями {@link AdminDoc}
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param teamService          объект класса {@link TeamService} - сервис, обрабатывающий запросы, приходящие со страницы
 *                             команды
 * @param utilsService         объект класса {@link UtilsService} - сервис, содержащий часто используемые методы без
 *                             привязки к конкретному сервису
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record EventService(TournamentRepository tournamentRepository, TeamRepository teamRepository,
                           CountryRepository countryRepository, MapPoolRepository mapPoolRepository,
                           AdminRepository adminRepository, PlayerRepository playerRepository,
                           TeamService teamService, UtilsService utilsService) {
    /**
     * Метод позволяет получить всю необходимую информацию для взаимодействия со страницей турнира
     *
     * @param event  название турнира, для которого получается информация
     * @param player ник игрока, который просматривает страницу
     * @return Объект класса {@link FullEventDTO}, содержащий всю информацию, необходимую для взаимодействия со
     * страницей турнира
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    public FullEventDTO getFullEvent(String event, String player) throws AppException {
        String eventName = replaceDashes(event);

        if (!tournamentRepository.existsByName(eventName))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(eventName);

        ChosenEventDTO chosenEventDTO = tournamentDocToChosenEvent(tournamentDoc);

        ArrayList<String> eventMapPool = getMapPoolArray(mapPoolRepository.findAll());

        ArrayList<MatchDTO> ongoingMatches = tournamentDoc.getSortedByTimeOngoingMatches();

        ArrayList<MatchTimeByDateDTO> upcomingMatches = tournamentDoc.getSortedByDateUpcomingMatches();

        ArrayList<MatchTimeByDateDTO> results = sortMatchesByDateTime(getAttendedEndedMatches(tournamentDoc, "", ""), "reversed");

        Boolean activeTour = isTeamRegistered(tournamentDoc, player);

        Boolean isCap = getIsCap(player, chosenEventDTO.getFormat());

        Boolean isAdmin = adminRepository.existsByAdminId(player);

        List<CountryDoc> countries = countryRepository.findAll();

        ArrayList<NameDTO> players = getPlayers(player);

        ParticipantDTO playerTeam = getPlayerTeam(player, chosenEventDTO.getParticipants(),
                tournamentDoc.getParticipantType());

        String mvp = tournamentDoc.getMvp();

        return new FullEventDTO(chosenEventDTO, eventMapPool, ongoingMatches, upcomingMatches, results, activeTour, isCap,
                isAdmin, countries, players, playerTeam, mvp);
    }


    /**
     * Метод используется при редактировании ещё не начавшегося турнира
     *
     * @param chosenEventDTO объект класса {@link ChosenEventDTO}, содержащий измененную информацию о турнире
     * @param oldEvent       старое название турнира (может совпадать с названием после изменения). Используется для
     *                       определения турнира для изменения
     * @return Объект класса {@link ChosenEventDTO}, одержащий информацию об измененном турнире
     * @throws AppException Если турнира с таким названием не существует в базе данных или новое название турнира уже
     *                      занято
     */
    public ChosenEventDTO editUpcomingEvent(ChosenEventDTO chosenEventDTO, String oldEvent) throws AppException {
        if (!tournamentRepository.existsByName(oldEvent))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

//        if (isEventAlreadyExist(oldEvent, chosenEventDTO.getEvent()))
//            throw new AppException("Такой турнир уже существует", HttpStatus.BAD_REQUEST);

        if (utilsService.isAlreadyExists(chosenEventDTO.getEvent()))
            throw new AppException("Такой турнир уже существует", HttpStatus.BAD_REQUEST);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(oldEvent);

        String[] splitDate = chosenEventDTO.getDate().split(" - ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        tournamentDoc.setCountry(chosenEventDTO.getCountry());
        tournamentDoc.setCity(chosenEventDTO.getCity());
        tournamentDoc.setDateStart(LocalDate.parse(splitDate[0], formatter));
        tournamentDoc.setDateEnd(LocalDate.parse(splitDate[1], formatter));
        tournamentDoc.setPrize(chosenEventDTO.getPrize());
        tournamentDoc.setFee(chosenEventDTO.getFee());
        tournamentDoc.setType(chosenEventDTO.getType());
        tournamentDoc.setFormat(chosenEventDTO.getFormat());

        if (!oldEvent.equals(chosenEventDTO.getEvent())) {
            tournamentRepository.deleteByName(oldEvent);
            tournamentDoc.setName(chosenEventDTO.getEvent());
        }

        tournamentRepository.save(tournamentDoc);

        return chosenEventDTO;
    }


    /**
     * Метод используется при редактировании заголовка текущего турнира
     *
     * @param editOngoingEventHeaderDTO объект класса {@link EditOngoingEventHeaderDTO}, содержащий измененную информацию
     *                                  о заголовке турнира
     * @param oldEvent                  старое название турнира (может совпадать с названием после изменения).
     *                                  Используется для определения турнира для изменения
     * @return Объект класса {@link EditOngoingEventHeaderDTO}, содержащий информацию об измененном заголовке турнира
     * @throws AppException Если турнира с таким названием не существует в базе данных или новое название турнира уже
     *                      занято
     */
    public EditOngoingEventHeaderDTO editOngoingEventHeader(EditOngoingEventHeaderDTO editOngoingEventHeaderDTO, String oldEvent) throws AppException {
        if (!tournamentRepository.existsByName(oldEvent))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

//        if (isEventAlreadyExist(oldEvent, editOngoingEventHeaderDTO.getEvent()))
//            throw new AppException("Такой турнир уже существует", HttpStatus.BAD_REQUEST);

        if (utilsService.isAlreadyExists(editOngoingEventHeaderDTO.getEvent()))
            throw new AppException("Такой турнир уже существует", HttpStatus.BAD_REQUEST);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(oldEvent);

        tournamentDoc.setName(editOngoingEventHeaderDTO.getEvent());
        tournamentDoc.setPrize(editOngoingEventHeaderDTO.getPrize());

        if (!oldEvent.equals(editOngoingEventHeaderDTO.getEvent())) {
            tournamentRepository.deleteByName(oldEvent);
            tournamentDoc.setName(editOngoingEventHeaderDTO.getEvent());
        }

        tournamentRepository.save(tournamentDoc);

        return editOngoingEventHeaderDTO;
    }


    /**
     * Метод добавляет новую заявку на участие в турнире в базу данных
     *
     * @param event          название турнира, на который регистрируется команда
     * @param participantDTO объект класса {@link ParticipantDTO}, содержащий информацию о зарегистрированной команде
     * @return Объект класса {@link Requests}, содержащий основную информацию о заявке команды
     * @throws AppException Если турнира с таким названием не существует в базе данных или данная команда уэе отправила
     *                      заявку
     */
    public Requests addNewRequest(String event, ParticipantDTO participantDTO) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        ArrayList<Requests> requests = tournamentDoc.getRequests();

        for (Requests request : requests) {
            if (request.getTeamName().equals(participantDTO.getTeamName()))
                throw new AppException("Такая команда уже зарегистрирована", HttpStatus.BAD_REQUEST);
        }

        Requests newRequest = participantDtoToRequest(participantDTO, requests.size());

        requests.add(newRequest);

        tournamentDoc.setRequests(requests);
        tournamentRepository.save(tournamentDoc);

        return newRequest;
    }


    /**
     * Метод позволяет изменить состав участников команды
     *
     * @param event         название турнира, в рамках которого меняется список участников
     * @param teamName      название команды, изменяющей список участников
     * @param chosenPlayers список ников игроков, которые будут участвовать в турнире
     * @return Обновленный список ников игроков, принимающих участие в турнире
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    public ArrayList<String> editChosenPlayers(String event, String teamName, ArrayList<String> chosenPlayers) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        ArrayList<Requests> requests = tournamentDoc.getRequests();

        for (Requests request : requests) {
            if (request.getTeamName().equals(teamName)) {
                request.setChosenPlayers(chosenPlayers);
            }
        }

        tournamentDoc.setRequests(requests);
        tournamentRepository.save(tournamentDoc);

        return chosenPlayers;
    }


    /**
     * Метод позволяет изменить ссылку на диск с фотографиями с турнира
     *
     * @param event   название турнира, у которого изменяется ссылка
     * @param diskUrl новая ссылка на диск
     * @return Новая ссылка на диск
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    public String editDiskUrl(String event, String diskUrl) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        StringBuilder sb = new StringBuilder(diskUrl);

        if (sb.charAt(0) == '\"' && sb.charAt(sb.length() - 1) == '\"') {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(0);
        }

        tournamentDoc.setDiskUrl(sb.toString());

        tournamentRepository.save(tournamentDoc);

        return diskUrl;
    }


    /**
     * Метод изменяет призовые места у турнира
     *
     * @param event       название турнира, у которого редактируются призовые места
     * @param prizePlaces писок объектов класса {@link PrizePlaces}, содержащих информацию об измененном распределении
     *                    призовых мест на турнире
     * @return Список объектов класса {@link PrizePlaces}, содержащих информацию об измененном распределении призовых
     * мест на турнире
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    public ArrayList<PrizePlaces> editPrizePlaces(String event, ArrayList<PrizePlaces> prizePlaces) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        tournamentDoc.setPrizePlaces(prizePlaces);

        tournamentRepository.save(tournamentDoc);

        return prizePlaces;
    }


    /**
     * Метод позволяет изменить информацию о проведении турнира - описание
     *
     * @param event       название турнира, у которого изменяется описание
     * @param description новое описание турнира
     * @return Измененное описание турнира
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    public String editEventDescription(String event, String description) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        StringBuilder sb = new StringBuilder(description);

        if (sb.charAt(0) == '\"' && sb.charAt(sb.length() - 1) == '\"') {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(0);
        }

        tournamentDoc.setDescription(sb.toString());

        tournamentRepository.save(tournamentDoc);

        return description;
    }


    /**
     * Метод позволяет изменить маппул турнира
     *
     * @param event   название турнира, у которого изменяется маппул
     * @param mapPool измененный список названий карт, доступных в рамках турнира - измененный маппул
     * @return Измененный список названий карт, доступных в рамках турнира - измененный маппул
     * @throws AppException Если турнира с таким названием не существует в базе данных
     */
    public ArrayList<String> editEventMapPool(String event, ArrayList<String> mapPool) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        tournamentDoc.setMapPool(mapPool);

        tournamentRepository.save(tournamentDoc);

        return mapPool;
    }


    /**
     * Метод позволяет изменить статус завяки команды на турнире
     *
     * @param event    название турнира, в рамках которого меняется статус команды
     * @param teamName название команды, у которой изменяется статус
     * @param status   новый статус команды на турнире:
     *                 <li><b>kicked</b> - команда исключена. Статус команды может принять данное значение только если
     *                 турнир уже начался</li>
     *                 <li><b>accepted</b> - заявка принята</li>
     *                 <li><b>await</b> - заявка на рассмотрении</li>
     * @return Объект класса {@link Requests}, содержащий информацию об измененной заявке команды
     * @throws AppException Если турнира с таким названием не существует в базе данных, или статус команды, или указана
     *                      незарегистрированная на турнире команда
     */
    public Requests editRequestStatus(String event, String teamName, String status) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        if (!(status.equals("kicked") || status.equals("accepted") || status.equals("await")))
            throw new AppException("Неверно указан статус команды", HttpStatus.BAD_REQUEST);

        ArrayList<Requests> requests = tournamentDoc.getRequests();

        Requests editedRequest = new Requests();

        boolean result = false;

        for (Requests request : requests) {
            if (request.getTeamName().equals(teamName)) {
                editedRequest = request;
                request.setStatus(status);
                result = true;
                break;
            }
        }

        if (!result)
            throw new AppException("Такой команды не зарегистрировано", HttpStatus.NOT_FOUND);

        tournamentDoc.setRequests(requests);
        tournamentRepository.save(tournamentDoc);

        return editedRequest;
    }


    /**
     * Метод позволяет удалить (отклонить) заявку команды у ещё не начавшегося турнира
     *
     * @param event    название турнира, в рамках которого удаляется (отклоняется) заявка команды
     * @param teamName название команды, заявка которой удаляется (отклоняется)
     * @return Объект класса {@link Requests}, содержащий информацию об удаленной заявке команды
     * @throws AppException Если турнира с таким названием не существует в базе данных или указана незарегистрированная
     *                      на турнире команда
     */
    public Requests deleteRequest(String event, String teamName) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        ArrayList<Requests> requests = tournamentDoc.getRequests();

        Requests deletedRequest = new Requests();

        boolean result = false;

        Iterator<Requests> i = requests.iterator();
        while (i.hasNext()) {
            Requests request = i.next();
            if (request.getTeamName().equals(teamName)) {
                deletedRequest = request;
                i.remove();
                result = true;
                break;
            }
        }

        if (!result)
            throw new AppException("Такой команды не зарегистрировано", HttpStatus.NOT_FOUND);

        tournamentDoc.setRequests(requests);
        tournamentRepository.save(tournamentDoc);

        return deletedRequest;
    }


    /**
     * Метод позволяет установить MVP турнира
     *
     * @param event  название турнира, в рамках которого игрок стал MVP
     * @param player ник игрока, ставшего MVP в рамках данного турнира
     * @return Ник игрока, ставшего MVP турнира
     * @throws AppException Если в базе данных не существует турнира с таким названием или пользователя с таким ником
     */
    public String editEventMVP(String event, String player) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        if (!playerRepository.existsByNick(player))
            throw new AppException("Такого игрока не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);
        tournamentDoc.setMvp(player);
        tournamentRepository.save(tournamentDoc);

        return player;
    }


    /**
     * Метод конвертирует объект класса {@link TournamentDoc} в объект класса {@link ChosenEventDTO}
     *
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, который необходимо
     *                      конвертировать в объект класса {@link ChosenEventDTO}
     * @return Объект класса {@link ChosenEventDTO}, содержащий информацию о турнире
     */
    private ChosenEventDTO tournamentDocToChosenEvent(TournamentDoc tournamentDoc) {
        return new ChosenEventDTO(
                tournamentDoc.getName(),
                countryRepository.findByCountryRU(tournamentDoc.getCountry()).getFlagPathMini(),
                tournamentDoc.getCountry(),
                getCity(tournamentDoc.getCity()),
                parseEventDate(tournamentDoc.getDateStart(), tournamentDoc.getDateEnd()),
                tournamentDoc.getFormat(),
                tournamentDoc.getType(),
                tournamentDoc.getRequests().size(),
                tournamentDoc.getTotal(),
                tournamentDoc.getFee(),
                tournamentDoc.getPrize(),
                tournamentDoc.getEventStatus(),
                tournamentDoc.getParticipantType(),
                tournamentDoc.getDiskUrl(),
                getParticipants(tournamentDoc),
                getPlayersFromTeams(tournamentDoc),
                tournamentDoc.getHeaderLink(),
                tournamentDoc.getLogoLink(),
                tournamentDoc.getTrophyLink(),
                tournamentDoc.getMvpLink(),
                tournamentDoc.getDescription(),
                getPrizePlaces(tournamentDoc),
                tournamentDoc.getMapPool()
        );
    }


    /**
     * Метод позволяет получить список объектов класса {@link TeamNickDTO}, ставящих в соответствие игрока и его команду
     *
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      необходимо получить список игроков и их команд
     * @return Список объектов класса {@link TeamNickDTO} - игроков команд-участниц турнира
     */
    private ArrayList<TeamNickDTO> getPlayersFromTeams(TournamentDoc tournamentDoc) {
        ArrayList<Requests> requests = tournamentDoc.getRequests();
        ArrayList<TeamNickDTO> participants = new ArrayList<>();

        String type = tournamentDoc.getParticipantType();
        for (Requests request : requests) {
            if (type.equals("player")) {
                String player = request.getTeamName();
                participants.add(new TeamNickDTO(player, getTeam(playerRepository.findByNick(player))));
            } else {
                String team = request.getTeamName();
                ArrayList<FlagNameSrcDTO> players = teamService.getTeamPlayers(team);
                for (FlagNameSrcDTO player : players) {
                    participants.add(new TeamNickDTO(player.getName(), team));
                }
            }
        }
        return participants;
    }


    /**
     * Метод позволяет получить список команд-участниц турнира
     *
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      необходимо получить список участников турнира
     * @return Список объектов класса {@link ParticipantDTO}, содержащих информацию о командах-участницах данного турнира
     */
    private ArrayList<ParticipantDTO> getParticipants(TournamentDoc tournamentDoc) {
        ArrayList<Requests> requests = tournamentDoc.getRequests();
        ArrayList<ParticipantDTO> participants = new ArrayList<>();

        String type = tournamentDoc.getParticipantType();
        for (Requests request : requests) {
            String tag;
            String team = "";
            if (type.equals("player")) {
                tag = request.getTeamName();
                team = getTeam(playerRepository.findByNick(tag));
            } else
                tag = teamRepository.findByTeamName(request.getTeamName()).getTag();

            participants.add(new ParticipantDTO(request.getRequest_id(), request.getTeamName(),
                    getParticipantStatus(request.getStatus(), tournamentDoc), request.getChosenPlayers(), type, tag,
                    team));
        }

        return participants;
    }


    /**
     * Метод позволяет перевести статус команды на турнире в нужный формат для отображения на frontend
     *
     * @param status        текущий статус команды на турнире
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      необходимо определить статус команды
     * @return Корректная форма статуса для отображения на frontend
     */
    private String getParticipantStatus(String status, TournamentDoc tournamentDoc) {
        if (status.equals("accepted") && !tournamentDoc.getEventStatus().equals("upcoming"))
            return "";
        return status;
    }


    /**
     * Метод позволяет перевести город проведения турнира в нужный формат для отображения на frontend
     *
     * @param city город проведения турнира
     * @return Корректная форма города для отображения на frontend
     */
    private String getCity(String city) {
        if (city.isEmpty())
            return "Выберите город";
        return city;
    }


    /**
     * Метод позволяет получить информацию о распределении призовых мест турнира
     *
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      необходимо получить список призовых мест
     * @return Список объектов класса {@link EventPrizePlaceDTO}, содержащих информацию о распределении призовых мест
     */
    private ArrayList<EventPrizePlaceDTO> getPrizePlaces(TournamentDoc tournamentDoc) {
        ArrayList<EventPrizePlaceDTO> eventPrizePlaceList = new ArrayList<>();
        ArrayList<PrizePlaces> prizePlaces = tournamentDoc.getPrizePlaces();
        for (PrizePlaces prizePlace : prizePlaces) {
            EventPrizePlaceDTO eventPrizePlace = new EventPrizePlaceDTO(prizePlace.getTeamName(), prizePlace.getPlace(),
                    prizePlace.getReward(), "");

            if (tournamentDoc.getParticipantType().equals("player") && prizePlace.getTeamName() != null &&
                    !prizePlace.getTeamName().isEmpty()) {
                eventPrizePlace.setTeam(getTeam(playerRepository.findByNick(prizePlace.getTeamName())));
            }

            eventPrizePlaceList.add(eventPrizePlace);
        }
        return eventPrizePlaceList;
    }


    /**
     * Метод позволяет определить, зарегистрирована ли команда или игрок на турнире
     *
     * @param tournamentDoc объект класса {@link TournamentDoc}, содержащий информацию о турнире, в рамках которого
     *                      необходимо узнать зарегистрирована ли команда или игрок на турнире
     * @param player        ник игрока, о котором необходимо узнать, зарегистрирован ли они или его команда на турнире
     * @return Зарегистрирована ли команда или игрок на турнире: <code>true</code>, если зарегистрирована;
     * <code>false</code> иначе
     */
    private Boolean isTeamRegistered(TournamentDoc tournamentDoc, String player) {
        if (!playerRepository.existsByNick(player))
            return false;

        String teamName = getTeam(playerRepository.findByNick(player));

        ArrayList<Requests> requests = tournamentDoc.getRequests();
        for (Requests request : requests) {
            if ((request.getTeamName().equals(teamName) || request.getTeamName().equals(player)) &&
                    !request.getStatus().equals("kicked"))
                return true;
        }
        return false;
    }


    /**
     * Метод позволяет определить, является ли пользователь капитаном команды
     *
     * @param player      ник пользователя, о котором необходимо узнать, является ли он капитаном
     * @param eventFormat формат турнира:
     *                    <li>1x1</li>
     *                    <li>2x2</li>
     *                    <li>5x5</li>
     * @return Является ли пользователь капитаном команды: <code>true</code>, если да; <code>false</code> иначе
     */
    private Boolean getIsCap(String player, String eventFormat) {
        if (!playerRepository.existsByNick(player))
            return false;

        if (eventFormat.equals("1x1"))
            return true;

        String teamName = getTeam(playerRepository.findByNick(player));
        if (teamName.isEmpty())
            return false;

        String teamCap = teamRepository.findByTeamName(teamName).getCaptain();

        return teamCap.equals(player);
    }


    /**
     * Метод позволяет получить список игроков команды пользователя
     *
     * @param player ник пользователя, список тиммейтов которого необходимо получить
     * @return Список объектов класса {@link NameDTO}, содержащих ники игроков команды пользователя
     */
    private ArrayList<NameDTO> getPlayers(String player) {
        ArrayList<NameDTO> players = new ArrayList<>();

        if (!playerRepository.existsByNick(player))
            return players;

        String teamName = getTeam(playerRepository.findByNick(player));

        if (teamName.isEmpty())
            return players;

        List<PlayerDoc> playerDocList = playerRepository.findAll();

        for (PlayerDoc playerDoc : playerDocList) {
            if (playerDoc.getSteam().isEmpty() || playerDoc.getFaceit().isEmpty() || playerDoc.getVk().isEmpty())
                continue;

            ArrayList<Rosters> rosters = playerDoc.getRosters();

            for (Rosters roster : rosters) {
                if (roster.getTeamName().equals(teamName) && roster.getExitDate() == null) {
                    players.add(new NameDTO(playerDoc.getNick()));
                    break;
                }
            }

            if (players.size() == 5) {
                break;
            }
        }

        return players;
    }


    /**
     * Метод позволяет получить информацию о команде пользователя
     *
     * @param player          ник пользователя, о чьей команде необходимо получить информацию
     * @param participantDTOS список объектов класса {@link ParticipantDTO}, содержащих информацию о командах-участницах
     *                        турнира
     * @param partType        тип участников турнира:
     *                        <li><b>player</b> - игрок, если формат турнира 1x1</li>
     *                        <li><b>team</b> - команда, если формат турнира 2x2 или 5x5</li>
     * @return Если пользователя с запрашиваемым ником не существует в базе данных или тип участников - <i>team</i>, а у
     * пользователя нет команды - <code>null</code>. В ином случае - объект класса {@link ParticipantDTO}, содержащий
     * информацию о команде пользователя
     */
    private ParticipantDTO getPlayerTeam(String player, ArrayList<ParticipantDTO> participantDTOS, String partType) {
        if (!playerRepository.existsByNick(player))
            return null;

        String teamName = getTeam(playerRepository.findByNick(player));

        if (partType.equals("team") && teamName.isEmpty())
            return null;

        for (ParticipantDTO participantDTO : participantDTOS) {
            if (participantDTO.getTeamName().equals(teamName))
                return participantDTO;
        }

        String tag;
        String team = "";
        String teamNameToSet;
        if (partType.equals("player")) {
            tag = player;
            team = teamName;
            teamNameToSet = player;
        } else {
            tag = teamRepository.findByTeamName(teamName).getTag();
            teamNameToSet = teamName;
        }

        return new ParticipantDTO(-1, teamNameToSet, "-", new ArrayList<>(), partType, tag, team);
    }


//    /**
//     * Метод позволяет определить, занято ли название турнира
//     *
//     * @param oldEvent старое название турнира (может совпадать с названием после изменения)
//     * @param newEvent новое название турнира, которое необходимо проверить на уникальность
//     * @return Занято ли название турнира: <code>true</code>, если да; <code>false</code>, если нет
//     */
//    private Boolean isEventAlreadyExist(String oldEvent, String newEvent) {
//        return (!tournamentRepository.existsByName(oldEvent) && tournamentRepository.existsByName(newEvent)) ||
//                playerRepository.existsByNick(newEvent) ||
//                teamRepository.existsByTag(newEvent) ||
//                teamRepository.existsByTeamName(newEvent);
//    }


    /**
     * Метод конвертирует объект класса {@link ParticipantDTO} в объект класса {@link Requests}
     *
     * @param participantDTO объект класса {@link ParticipantDTO}, содержащий информацию о команде-участнице турнира,
     *                       который необходимо конвертировать в объект класса {@link Requests}
     * @param size           общее число поданных заявок на турнир
     * @return Объект класса {@link Requests}, содержащий информацию о заявке команды на участие в турнире
     */
    private Requests participantDtoToRequest(ParticipantDTO participantDTO, int size) {
        return new Requests(size + 1, participantDTO.getTeamName(), participantDTO.getStatus(),
                participantDTO.getChosenPlayers());
    }
}
