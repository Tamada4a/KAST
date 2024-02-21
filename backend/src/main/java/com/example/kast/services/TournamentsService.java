package com.example.kast.services;


import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.controllers.dto.tournaments.*;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.*;
import com.example.kast.mongo_collections.embedded.Requests;
import com.example.kast.mongo_collections.interfaces.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.kast.utils.EventUtils.getMapPoolArray;
import static com.example.kast.utils.EventUtils.parseEventDate;
import static com.example.kast.utils.Utils.*;


/**
 * Данный класс является сервисом, реализующим логику обработки запросов, связанных со страницей "Турниры"
 *
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @param countryRepository    интерфейс для взаимодействия с сущностями {@link CountryDoc}
 * @param adminRepository      интерфейс для взаимодействия с сущностями {@link AdminDoc}
 * @param mapPoolRepository    интерфейс для взаимодействия с сущностями {@link MapPoolDoc}
 * @param teamRepository       интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param imageService         объект класса {@link ImageService} - сервис, обрабатывающий запросы, связанные с изображениями
 * @param utilsService         объект класса {@link UtilsService} - сервис, содержащий часто используемые методы без
 *                             привязки к конкретному сервису
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record TournamentsService(TournamentRepository tournamentRepository, CountryRepository countryRepository,
                                 AdminRepository adminRepository, MapPoolRepository mapPoolRepository,
                                 TeamRepository teamRepository, PlayerRepository playerRepository,
                                 ImageService imageService, UtilsService utilsService) {
    /**
     * Метод позволяет получить информацию, необходимую для взаимодействия со страницей "Турниры" на frontend
     *
     * @param player ник игрока, который просматривает страницу
     * @return Объект класса {@link FullTournamentsDTO}, содержащий информацию, необходимую для взаимодействия со
     * страницей "Турниры" на frontend
     */
    public FullTournamentsDTO getFullTournaments(String player) {
        List<TournamentDoc> tournamentDocList = tournamentRepository.findAll();

        ArrayList<EndedEventsByDateDTO> endedEvents = getEndedEvents(tournamentDocList);
        ArrayList<FeaturedEventsByDateDTO> featuredEvents = getFeaturedEvents(tournamentDocList);
        ArrayList<OngoingEventDTO> ongoingEvents = getOngoingEvents(tournamentDocList);

        Boolean isAdmin = adminRepository.existsByAdminId(player);

        List<CountryDoc> countries = countryRepository.findAll();
        ArrayList<String> mapPool = getMapPoolArray(mapPoolRepository.findAll());

        return new FullTournamentsDTO(endedEvents, featuredEvents, ongoingEvents, isAdmin, countries, mapPool);
    }


    /**
     * Метод позволяет редактировать текущий турнир
     *
     * @param ongoingEventDTO объект класса {@link OngoingEventDTO}, содержащий измененную информацию о текущем турнире
     * @param oldEvent        старое название турнира (может совпадать с названием после изменения). Используется для
     *                        определения турнира для изменения
     * @return Объект класса {@link OngoingEventDTO}, содержащий информацию об измененном текущем турнире
     * @throws AppException Если турнира со старым названием не существует в базе данных или новое название турнира
     *                      занято
     */
    public OngoingEventDTO editOngoingEvent(OngoingEventDTO ongoingEventDTO, String oldEvent) throws AppException {
        if (!tournamentRepository.existsByName(oldEvent))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

//        if ((tournamentRepository.existsByName(ongoingEventDTO.getEvent()) && !tournamentRepository.existsByName(oldEvent)) ||
//                playerRepository.existsByNick(ongoingEventDTO.getEvent()) ||
//                teamRepository.existsByTag(ongoingEventDTO.getEvent()) ||
//                teamRepository.existsByTeamName(ongoingEventDTO.getEvent()))
        if (utilsService.isAlreadyExists(ongoingEventDTO.getEvent()))
            throw new AppException("Такой турнир уже существует", HttpStatus.BAD_REQUEST);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(oldEvent);

        tournamentDoc.setFee(ongoingEventDTO.getFee());
        tournamentDoc.setPrize(ongoingEventDTO.getPrize());
        tournamentDoc.setDiskUrl(ongoingEventDTO.getYaDiskUrl());
        tournamentDoc.setPrizePlaces(ongoingEventDTO.getPrizePlaces());

        if (!oldEvent.equals(ongoingEventDTO.getEvent())) {
            tournamentRepository.deleteByName(oldEvent);
            tournamentDoc.setName(ongoingEventDTO.getEvent());
        }

        tournamentRepository.save(tournamentDoc);

        return ongoingEventDTO;
    }


    /**
     * Метод позволяет редактировать будущий турнир
     *
     * @param featuredEventDTO объект класса {@link FeaturedEventDTO}, содержащий измененную информацию о будущем турнире
     * @param oldEvent         старое название турнира (может совпадать с названием после изменения). Используется для
     *                         определения турнира для изменения
     * @return Объект класса {@link FeaturedEventDTO}, содержащий информацию об измененном будущем турнире
     * @throws AppException Если турнира со старым названием не существует в базе данных или новое название турнира
     *                      занято
     */
    public FeaturedEventDTO editFeaturedEvent(FeaturedEventDTO featuredEventDTO, String oldEvent) throws AppException {
        if (!tournamentRepository.existsByName(oldEvent))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        if ((tournamentRepository.existsByName(featuredEventDTO.getEvent()) && !tournamentRepository.existsByName(oldEvent)) ||
                playerRepository.existsByNick(featuredEventDTO.getEvent()) ||
                teamRepository.existsByTag(featuredEventDTO.getEvent()) ||
                teamRepository.existsByTeamName(featuredEventDTO.getEvent()))
            throw new AppException("Такой турнир уже существует", HttpStatus.BAD_REQUEST);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(oldEvent);

        String[] splitDate = featuredEventDTO.getDate().split(" - ");

        tournamentDoc.setCountry(featuredEventDTO.getCountry());
        tournamentDoc.setCity(featuredEventDTO.getCity());
        tournamentDoc.setDateStart(parseStringDateToLocalDate(splitDate[0].split("\\.")));
        tournamentDoc.setDateEnd(parseStringDateToLocalDate(splitDate[1].split("\\.")));
        tournamentDoc.setFormat(featuredEventDTO.getFormat());
        tournamentDoc.setType(featuredEventDTO.getType());
        tournamentDoc.setTotal(featuredEventDTO.getTotal());
        tournamentDoc.setFee(featuredEventDTO.getFee());
        tournamentDoc.setPrize(featuredEventDTO.getPrize());
        tournamentDoc.setDescription(featuredEventDTO.getDescription());
        tournamentDoc.setPrizePlaces(featuredEventDTO.getPrizePlaces());
        tournamentDoc.setMapPool(featuredEventDTO.getMapPool());

        if (!oldEvent.equals(featuredEventDTO.getEvent())) {
            tournamentRepository.deleteByName(oldEvent);
            tournamentDoc.setName(featuredEventDTO.getEvent());
        }

        tournamentRepository.save(tournamentDoc);

        return featuredEventDTO;
    }


    /**
     * Метод позволяет удалить турнир из базы данных
     *
     * @param event название турнира, который необходимо удалить
     * @return объект класса {@link TournamentDoc}, содержащий информацию об удаленном турнире
     * @throws AppException Если турнира со старым названием не существует в базе данных
     */
    public TournamentDoc deleteEvent(String event) throws AppException {
        if (!tournamentRepository.existsByName(event))
            throw new AppException("Такого турнира не существует", HttpStatus.NOT_FOUND);

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);

        imageService.deleteImage(tournamentDoc.getHeaderLink());
        imageService.deleteImage(tournamentDoc.getMvpLink());
        imageService.deleteImage(tournamentDoc.getLogoLink());
        imageService.deleteImage(tournamentDoc.getTrophyLink());

        tournamentRepository.deleteByName(event);

        return tournamentDoc;
    }


    /**
     * Метод позволяет создать новый турнир
     *
     * @param newEvent объект класса {@link FeaturedEventDTO}, содержащий информацию о созданном турнире
     * @return объект класса {@link FeaturedEventDTO},
     * содержащий информацию о новом турнире
     * @throws AppException Если название турнира занято
     */
    public TournamentDoc createNewEvent(FeaturedEventDTO newEvent) throws AppException {
        if (tournamentRepository.existsByName(newEvent.getEvent()) ||
                playerRepository.existsByNick(newEvent.getEvent()) ||
                teamRepository.existsByTag(newEvent.getEvent()) ||
                teamRepository.existsByTeamName(newEvent.getEvent()))
            throw new AppException("Такой турнир уже существует", HttpStatus.BAD_REQUEST);

        TournamentDoc eventToSave = featuredEventToTournamentDoc(newEvent);
        tournamentRepository.save(eventToSave);

        return eventToSave;
    }


    /**
     * Метод конвертирует объект класса {@link FeaturedEventDTO} в объект класса {@link TournamentDoc}
     *
     * @param newEvent объект класса {@link FeaturedEventDTO}, содержащий информацию о новом турнире, который необходимо
     *                 конвертировать в объект класса {@link TournamentDoc}
     * @return Объект класса {@link TournamentDoc}, содержащий информацию о новом турнире для сохранения в базу данных
     */
    private TournamentDoc featuredEventToTournamentDoc(FeaturedEventDTO newEvent) {
        String[] splitDate = newEvent.getDate().split(" - ");

        return new TournamentDoc(
                tournamentRepository.findAll().size() + 1,
                newEvent.getEvent(),
                parseStringDateToLocalDate(splitDate[0].split("\\.")),
                parseStringDateToLocalDate(splitDate[1].split("\\.")),
                newEvent.getType(),
                newEvent.getFormat(),
                newEvent.getCountry(),
                newEvent.getCity(),
                "/events_logo/" + imageService.changeFileName(newEvent.getEventFile(), newEvent.getEvent()),
                "/trophies/" + imageService.changeFileName(newEvent.getTrophyFile(), newEvent.getEvent()),
                "/events_header/" + imageService.changeFileName(newEvent.getHeaderFile(), newEvent.getEvent()),
                newEvent.getPrize(),
                newEvent.getFee(),
                newEvent.getTotal(),
                newEvent.getDescription(),
                "",
                "",
                "/events_mvp/" + imageService.changeFileName(newEvent.getMvpFile(), newEvent.getEvent()),
                newEvent.getMapPool(),
                new ArrayList<>(),
                newEvent.getPrizePlaces(),
                new ArrayList<>()
        );
    }


    /**
     * Метод приводит полученную JSON-строку, представляющую собой список объектов, содержащих информацию о завершенных
     * турнирах, к списку объектов класса {@link EndedEventDTO}, а затем сортирует их по дате начала
     *
     * @param tournamentDocList список турниров, среди которых проводится поиск завершенных турниров
     * @return Список объектов класса {@link EndedEventsByDateDTO}, содержащих информацию о прошедших турнирах,
     * соответствующих определенной дате
     */
    private ArrayList<EndedEventsByDateDTO> getEndedEvents(List<TournamentDoc> tournamentDocList) {
        Type listType = new TypeToken<ArrayList<EndedEventDTO>>() {
        }.getType();
        ArrayList<EndedEventDTO> endedEvents = new GsonBuilder().create().fromJson(getEventsByType(tournamentDocList, "ended"), listType);

        Collections.reverse(endedEvents);

        return sortEndedEventsByDate(endedEvents);
    }


    /**
     * Метод конвертирует объект класса {@link TournamentDoc} в объект класса {@link EndedEventDTO}
     *
     * @param tournament объект класса {@link TournamentDoc}, содержащий информацию о турнире, который необходимо
     *                   конвертировать в объект класса {@link EndedEventDTO}
     * @return Объект класса {@link EndedEventDTO}, содержащий информацию о завершенном турнире
     */
    private EndedEventDTO tournamentDocToEndedEvent(TournamentDoc tournament) {
        return new EndedEventDTO(
                tournament.getName(),
                countryRepository.findByCountryRU(tournament.getCountry()).getFlagPathMini(),
                tournament.getCountry(),
                tournament.getCity(),
                parseEventDate(tournament.getDateStart(), tournament.getDateEnd()),
                tournament.getFormat(),
                tournament.getType(),
                tournament.getRequests().size(),
                tournament.getTotal(),
                tournament.getFee(),
                tournament.getPrize()
        );
    }


    /**
     * Метод приводит полученную JSON-строку, представляющую собой список объектов, содержащих информацию о будущих
     * турнирах, к списку объектов класса {@link FeaturedEventDTO}, а затем сортирует их по дате начала
     *
     * @param tournamentDocList список турниров, среди которых проводится поиск будущих турниров
     * @return Список объектов класса {@link FeaturedEventsByDateDTO}, содержащих информацию о будущих турнирах,
     * соответствующих определенной дате
     */
    private ArrayList<FeaturedEventsByDateDTO> getFeaturedEvents(List<TournamentDoc> tournamentDocList) {
        Type listType = new TypeToken<ArrayList<FeaturedEventDTO>>() {
        }.getType();
        ArrayList<FeaturedEventDTO> featuredEvents = new Gson().fromJson(getEventsByType(tournamentDocList, "upcoming"), listType);

        return sortFeaturedEventsByDate(featuredEvents);
    }


    /**
     * Метод конвертирует объект класса {@link TournamentDoc} в объект класса {@link FeaturedEventDTO}
     *
     * @param tournament объект класса {@link TournamentDoc}, содержащий информацию о турнире, который необходимо
     *                   конвертировать в объект класса {@link FeaturedEventDTO}
     * @return Объект класса {@link FeaturedEventDTO}, содержащий информацию о будущем турнире
     */
    private FeaturedEventDTO tournamentDocToFeaturedEvent(TournamentDoc tournament) {
        return new FeaturedEventDTO(
                new EndedEventDTO(
                        tournament.getName(),
                        countryRepository.findByCountryRU(tournament.getCountry()).getFlagPathMini(),
                        tournament.getCountry(),
                        tournament.getCity(),
                        getCorrectEventDate(tournament.getDateStart(), tournament.getDateEnd()),
                        tournament.getFormat(),
                        tournament.getType(),
                        tournament.getRequests().size(),
                        tournament.getTotal(),
                        tournament.getFee(),
                        tournament.getPrize()
                ),
                tournament.getHeaderLink(),
                tournament.getLogoLink(),
                tournament.getTrophyLink(),
                tournament.getMvpLink(),
                tournament.getDescription(),
                tournament.getPrizePlaces(),
                tournament.getMapPool()
        );
    }


    /**
     * Метод приводит полученную JSON-строку, представляющую список объектов, содержащих информацию о текущих турнирах,
     * к списку объектов класса {@link OngoingEventDTO}
     *
     * @param tournamentDocList список турниров, среди которых проводится поиск текущих турниров
     * @return Список объектов класса {@link OngoingEventDTO}, содержащих информацию о текущих турнирах, отсортированных
     * по дате начала
     */
    private ArrayList<OngoingEventDTO> getOngoingEvents(List<TournamentDoc> tournamentDocList) {
        Type listType = new TypeToken<ArrayList<OngoingEventDTO>>() {
        }.getType();
        return new Gson().fromJson(getEventsByType(tournamentDocList, "ongoing"), listType);
    }


    /**
     * Метод конвертирует объект класса {@link TournamentDoc} в объект класса {@link OngoingEventDTO}
     *
     * @param tournament объект класса {@link TournamentDoc}, содержащий информацию о турнире, который необходимо
     *                   конвертировать в объект класса {@link OngoingEventDTO}
     * @return Объект класса {@link OngoingEventDTO}, содержащий информацию о текущем турнире
     */
    private OngoingEventDTO tournamentDocToOngoingEvent(TournamentDoc tournament) {
        ArrayList<NameDTO> teams = new ArrayList<>();

        ArrayList<Requests> requests = tournament.getRequests();

        for (Requests request : requests) {
            teams.add(new NameDTO(request.getTeamName()));
        }
        return new OngoingEventDTO(
                new EndedEventDTO(
                        tournament.getName(),
                        countryRepository.findByCountryRU(tournament.getCountry()).getFlagPathMini(),
                        tournament.getCountry(),
                        tournament.getCity(),
                        getCorrectEventDate(tournament.getDateStart(), tournament.getDateEnd()),
                        tournament.getFormat(),
                        tournament.getType(),
                        tournament.getRequests().size(),
                        tournament.getTotal(),
                        tournament.getFee(),
                        tournament.getPrize()
                ),
                tournament.getHeaderLink(),
                tournament.getLogoLink(),
                tournament.getTrophyLink(),
                tournament.getMvpLink(),
                tournament.getDiskUrl(),
                teams,
                tournament.getPrizePlaces()
        );
    }


    /**
     * Метод позволяет получить список турниров, отсортированных по дате начала
     *
     * @param tournamentDocList список турниров, среди которых проводится поиск турниров, соответствующих запрашиваемому
     *                          типу
     * @param type              тип турниров, который необходимо получить:
     *                          <li><b>ongoing</b> - текущие турниры</li>
     *                          <li><b>upcoming</b> - будущие турниры</li>
     *                          <li><b>ended</b> - завершенные турниры</li>
     * @return JSON-строка, представляющая собой список объектов, содержащих информацию о турнирах, соответствующих
     * запрашиваемому типу и отсортированных по дате начала
     */
    private String getEventsByType(List<TournamentDoc> tournamentDocList, String type) {
        ArrayList<Object> eventsList = new ArrayList<>();

        for (TournamentDoc tournament : tournamentDocList) {
            if (tournament.getEventStatus().equals(type)) {
                switch (type) {
                    case "ended" -> eventsList.add(tournamentDocToEndedEvent(tournament));
                    case "upcoming" -> eventsList.add(tournamentDocToFeaturedEvent(tournament));
                    case "ongoing" -> eventsList.add(tournamentDocToOngoingEvent(tournament));
                }
            }
        }

        if (!eventsList.isEmpty()) {
            switch (type) {
                case "ended" -> eventsList.sort(Comparator.comparingInt(o -> eventDateToEpochDays(((EndedEventDTO) o).getDate())));
                case "upcoming" -> eventsList.sort(Comparator.comparingInt(o -> eventDateToEpochDays(((FeaturedEventDTO) o).getDate())));
                case "ongoing" -> eventsList.sort(Comparator.comparingInt(o -> eventDateToEpochDays(((OngoingEventDTO) o).getDate())));
            }
        }

        return new Gson().toJson(eventsList);
    }


    /**
     * Метод позволяет получить корректную дату проведения турнира. В зависимости от даты проведения возвращается строка
     * формата <i>дд.мм.гггг - дд.мм.гггг</i> или <i>дд.мм - дд.мм</i>. <br></br>
     * Например, дата начала турнира <i>14.01.2003</i>, а дата окончания - <i>17.01.2004</i>. На выходе будет строка
     * формата <i>14.01.2003 - 17.01.2004</i>. <br></br>
     * Если дата начала турнира <i>14.01.2003</i>, а дата окончания <i>20.02.2003</i>, то на выходе получим
     * <i>14.01 - 20.02</i>
     *
     * @param start дата начала турнира
     * @param end   дата окончания турнира
     * @return Если год окончания турнира больше года начала турнира или турнир начинается в следующем году - строка
     * формата <i>дд.мм.гггг - дд.мм.гггг</i>.<br></br>
     * Если турнир проходит в этом году - строка формата <i>дд.мм - дд.мм</i>
     */
    private String getCorrectEventDate(LocalDate start, LocalDate end) {
        if (end.getYear() > start.getYear() || start.getYear() > LocalDate.now().getYear())
            return parseEventDate(start, end);

        return String.format("%s.%s - %s.%s",
                fixNumber(start.getDayOfMonth()),
                fixNumber(start.getMonthValue()),
                fixNumber(end.getDayOfMonth()),
                fixNumber(end.getMonthValue()));
    }


    /**
     * Метод переводит дату начала турнира в количество дней, прошедших с <i>01.01.1970</i>
     *
     * @param date дата проведения турнира в формате <i>дд.мм.гггг - дд.мм.гггг</i> или <i>дд.мм - дд.мм</i> в
     *             зависимости от даты проведения турнира: <br></br>
     *             Если год окончания турнира больше года начала турнира или
     *             турнир начинается в следующем году - строка формата <i>дд.мм.гггг - дд.мм.гггг</i>.<br></br>
     *             Если турнир проходит в этом году - строка формата <i>дд.мм - дд.мм</i>
     * @return Количество дней, прошедших с <i>01.01.1970</i>
     */
    private int eventDateToEpochDays(String date) {
        String[] dateSplit = date.split(" - ");
        String[] dateStartSplit = dateSplit[0].split("\\.");

        if (dateStartSplit.length == 3) {
            return parseStringDateToEpochDays(dateStartSplit[0] + "." + dateStartSplit[1] + "." + dateStartSplit[2]);
        }
        return parseStringDateToEpochDays(dateSplit[0] + "." + LocalDate.now().getYear());
    }


    /**
     * Метод позволяет получить из строкового представления даты объект класса {@link LocalDate}
     *
     * @param splitDate массив строк, содержащий день, месяц и год
     * @return Объект класса {@link LocalDate}, соответствующий исходному массиву
     */
    private LocalDate parseStringDateToLocalDate(String[] splitDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        if (splitDate.length == 3) {
            return LocalDate.parse(splitDate[0] + "." + splitDate[1] + ".20" + splitDate[2], formatter);
        }
        return LocalDate.parse(splitDate[0] + "." + splitDate[1] + "." + LocalDate.now().getYear(), formatter);
    }


    /**
     * Метод приводит полученную JSON-строку, представляющую собой список объектов, содержащих информацию о завершенных
     * турнирах, распределенных по соответствующим датам типа <i>Месяц год</i>, к списку объектов класса {@link EndedEventsByDateDTO}
     *
     * @param endedEvents список объектов класса {@link EndedEventDTO}, содержащий информацию о завершенных турнирах
     * @return Список объектов класса {@link EndedEventsByDateDTO}, содержащих информацию о прошедших турнирах,
     * соответствующих определенной дате
     */
    private ArrayList<EndedEventsByDateDTO> sortEndedEventsByDate(ArrayList<EndedEventDTO> endedEvents) {
        Type listType = new TypeToken<ArrayList<EndedEventsByDateDTO>>() {
        }.getType();
        ArrayList<EndedEventsByDateDTO> sortedEvents = new Gson().fromJson(sortEventsByDateByType(endedEvents, "endedEvents"), listType);
        Collections.reverse(sortedEvents);

        return sortedEvents;
    }


    /**
     * Метод приводит полученную JSON-строку, представляющую собой список объектов, содержащих информацию о будущих
     * турнирах, распределенных по соответствующим датам типа <i>Месяц год</i>, к списку объектов класса
     * {@link FeaturedEventsByDateDTO}
     *
     * @param featuredEvents список объектов класса {@link FeaturedEventDTO}, содержащий информацию о будущих турнирах
     * @return Список объектов класса {@link FeaturedEventsByDateDTO}, содержащих информацию о будущих турнирах,
     * соответствующих определенной дате
     */
    private ArrayList<FeaturedEventsByDateDTO> sortFeaturedEventsByDate(ArrayList<FeaturedEventDTO> featuredEvents) {
        Type listType = new TypeToken<ArrayList<FeaturedEventsByDateDTO>>() {
        }.getType();
        return new Gson().fromJson(sortEventsByDateByType(featuredEvents, "featuredEvents"), listType);
    }


    /**
     * Метод позволяет распределить турниры по соответствующим датам формата <i>Месяц год</i>
     *
     * @param objectEventsList список турниров, которые необходимо распределить по датам
     * @param type             тип турниров, которые необходимо распределить:
     *                         <li><b>featuredEvents</b> - будущие турниры. В данном случае происходит сортировка
     *                         объектов класса {@link FeaturedEventDTO}, содержащих информацию о будущих турнирах</li>
     *                         <li><b>endedEvents</b> - завершенные турниры. В данном случае происходит сортировка
     *                         объектов класса {@link EndedEventDTO}, содержащих информацию о прошедших турнирах</li>
     * @return JSON-строка, представляющая собой список объектов, содержащих информацию о турнирах, распределенных по
     * соответствующим датам формата <i>Месяц год</i>
     */
    private String sortEventsByDateByType(Object objectEventsList, String type) {
        ArrayList<Object> castedObjectEventsList = (ArrayList<Object>) objectEventsList;

        ArrayList<Object> sortedEvents = new ArrayList<>();

        for (Object objectEvent : castedObjectEventsList) {
            String date = switch (type) {
                case "featuredEvents" -> getHeaderDate(((FeaturedEventDTO) objectEvent).getDate(), ((FeaturedEventDTO) objectEvent).getEvent());
                case "endedEvents" -> getHeaderDate(((EndedEventDTO) objectEvent).getDate(), ((EndedEventDTO) objectEvent).getEvent());
                default -> "";
            };
            int idx = getDateIndexFromList(sortedEvents, date, type);
            if (idx != -1) {
                if (type.equals("featuredEvents")) {
                    ArrayList<FeaturedEventDTO> eventsToSet = new ArrayList<>(((FeaturedEventsByDateDTO) sortedEvents.get(idx)).getEvents());
                    eventsToSet.add((FeaturedEventDTO) objectEvent);
                    ((FeaturedEventsByDateDTO) sortedEvents.get(idx)).setEvents(eventsToSet);
                } else {
                    ArrayList<EndedEventDTO> eventsToSet = new ArrayList<>(((EndedEventsByDateDTO) sortedEvents.get(idx)).getEvents());
                    eventsToSet.add((EndedEventDTO) objectEvent);
                    ((EndedEventsByDateDTO) sortedEvents.get(idx)).setEvents(eventsToSet);
                }
            } else {
                Object newEventByDate = switch (type) {
                    case "featuredEvents" -> new FeaturedEventsByDateDTO(date, List.of((FeaturedEventDTO) objectEvent));
                    case "endedEvents" -> new EndedEventsByDateDTO(date, List.of((EndedEventDTO) objectEvent));
                    default -> null;
                };
                sortedEvents.add(newEventByDate);
            }
        }

        if (type.equals("featuredEvents"))
            sortedEvents.sort(Comparator.comparingInt(o -> parseHeaderDateToEpochDays(((FeaturedEventsByDateDTO) o).getDate())));
        else
            sortedEvents.sort(Comparator.comparingInt(o -> parseHeaderDateToEpochDays(((EndedEventsByDateDTO) o).getDate())));

        return new Gson().toJson(sortedEvents);
    }


    /**
     * Метод позволяет получить заголовок даты начала турнира в формате <i>Месяц год</i>
     *
     * @param date  дата проведения турнира в формате <i>дд.мм.гггг - дд.мм.гггг</i> или <i>дд.мм - дд.мм</i> в
     *              зависимости от дат проведения турнира:<br></br>
     *              Если год окончания турнира больше года начала турнира или
     *              турнир начинается в следующем году - строка формата <i>дд.мм.гггг - дд.мм.гггг</i>.<br></br>
     *              Если турнир проходит в этом году - строка формата <i>дд.мм - дд.мм</i>
     * @param event название турнира, для которого определяется заголовок
     * @return Заголовок даты начала турнира в формате <i>Месяц год</i>
     */
    private String getHeaderDate(String date, String event) {
        String[] splitDate = date.split(" - ");
        String[] splitStart = splitDate[0].split("\\.");

        if (splitStart.length == 3) {
            return getMonthName(Integer.parseInt(splitStart[1])) + " " + splitStart[2];
        }

        TournamentDoc tournamentDoc = tournamentRepository.findByName(event);
        return getMonthName(Integer.parseInt(splitStart[1])) + " " + tournamentDoc.getDateStart().getYear();
    }


    /**
     * Метод переводит заголовок даты начала турнира формата <i>Месяц год</i> в количество дней, прошедших с
     * <i>01.01.1970</i>
     *
     * @param headerDate заголовок даты начала турнира в формате <i>Месяц год</i>
     * @return Количество дней, прошедших с <i>01.01.1970</i>
     */
    private int parseHeaderDateToEpochDays(String headerDate) {
        String[] splitDate = headerDate.split(" ");

        LocalDate newDate = LocalDate.of(Integer.parseInt(splitDate[1]), getMonthIndexByName(splitDate[0]), 1);

        return (int) newDate.toEpochDay();
    }


    /**
     * Метод позволяет получить порядковый номер месяца по его названию. Например: <i>Январь - 1</i>
     *
     * @param month название месяца
     * @return Порядковый номер месяца
     */
    private int getMonthIndexByName(String month) {
        return switch (month) {
            case "Январь" -> 1;
            case "Февраль" -> 2;
            case "Март" -> 3;
            case "Апрель" -> 4;
            case "Май" -> 5;
            case "Июнь" -> 6;
            case "Июль" -> 7;
            case "Август" -> 8;
            case "Сентябрь" -> 9;
            case "Октябрь" -> 10;
            case "Ноябрь" -> 11;
            case "Декабрь" -> 12;
            default -> -1;
        };
    }
}