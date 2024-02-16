package com.example.kast.mongo_collections.documents;


import com.example.kast.controllers.dto.matches.MatchDTO;
import com.example.kast.controllers.dto.matches.MatchTimeByDateDTO;
import com.example.kast.controllers.dto.matches.MatchTimeDTO;
import com.example.kast.controllers.dto.tab.EventMatchDTO;
import com.example.kast.mongo_collections.embedded.Matches;
import com.example.kast.mongo_collections.embedded.PrizePlaces;
import com.example.kast.mongo_collections.embedded.Requests;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;

import static com.example.kast.utils.MatchUtils.parseMatchTime;
import static com.example.kast.utils.MatchUtils.sortMatchesByDateTime;
import static com.example.kast.utils.Utils.fixNumber;
import static com.example.kast.utils.Utils.parseMatchDate;


/**
 * Данный класс описывает сущность <code>Tournament</code> из базы данных MongoDB. Содержит информацию о турнире
 *
 * @author Кирилл "Tamada" Симовин, Александр "ugly4" Федякин
 */
@Document("Tournament")
@Getter
@Setter
@AllArgsConstructor
public class TournamentDoc {
    /**
     * ID турнира. Идентификатор
     */
    @Id
    private Integer id;

    /**
     * Название турнира
     */
    private String name;

    /**
     * Дата начала турнира
     */
    private LocalDate dateStart;

    /**
     * Дата окончания турнира
     */
    private LocalDate dateEnd;

    /**
     * Тип турнира:
     * <li>Lan</li>
     * <li>Online</li>
     */
    private String type;

    /**
     * Формат проведения турнира:
     * <li>1x1</li>
     * <li>2x2</li>
     * <li>5x5</li>
     */
    private String format;

    /**
     * Страна проведения турнира
     */
    private String country;

    /**
     * Город проведения турнира
     */
    private String city;

    /**
     * Ссылка на логотип турнира на сервере
     */
    private String logoLink;

    /**
     * Ссылка на изображение трофея турнира на сервере
     */
    private String trophyLink;

    /**
     * Ссылка на хедер турнира на сервере
     */
    private String headerLink;

    /**
     * Призовой фонд турнира
     */
    private String prize;

    /**
     * Взнос с команды для участия в турнире
     */
    private String fee;

    /**
     * Максимальное число команд-участниц
     */
    private Integer total;

    /**
     * Описание турнира
     */
    private String description;

    /**
     * Ссылка на диск с фотографиями с турнира
     */
    private String diskUrl;

    /**
     * Ник игрока, ставшего MVP турнира
     */
    private String mvp;

    /**
     * Ссылка на изображение медали MVP турнира на сервере
     */
    private String mvpLink;

    /**
     * Маппул турнира
     */
    private ArrayList<String> mapPool;

    /**
     * Список объектов класса {@link Requests}, содержащих информацию об участниках турнира
     */
    private ArrayList<Requests> requests;

    /**
     * Список объектов класса {@link PrizePlaces} - призовых мест турнира
     */
    private ArrayList<PrizePlaces> prizePlaces;

    /**
     * Список объектов класса {@link Matches}, содержащих информацию о матчах, проходящих или проходивших в рамках
     * турнира
     */
    private ArrayList<Matches> matches;


    /**
     * Переопределенный метод строкового представления объекта класса {@link TournamentDoc}
     *
     * @return Строковое представление объекта класса {@link TournamentDoc}
     */
    @Override
    public String toString() {
        String dayStart = fixNumber(dateStart.getDayOfMonth());
        String monthStart = fixNumber(dateStart.getMonthValue());
        String dayEnd = fixNumber(dateEnd.getDayOfMonth());
        String monthEnd = fixNumber(dateEnd.getMonthValue());

        return String.format("Tournament(id=%s, name=%s, dateStart=%s-%s, dateEnd=%s-%s-%d, type=%s, format=%s," +
                        "country=%s, city=%s, logoLink=%s, trophyLink=%s, headerLink=%s, prize=%s, fee=%s, " +
                        "total=%s, description=%s, diskUrl=%s, mvp=%s, mvpLink=%s, mapPool=%s, requests=%s, " +
                        "prizePlaces=%s, matches=%s)",
                id, name, dayStart, monthStart, dayEnd, monthEnd, dateEnd.getYear(), type, format, country,
                city, logoLink, trophyLink, headerLink, prize, fee, total, description, diskUrl, mvp, mvpLink,
                mapPool.toString(), requests.toString(), prizePlaces.toString(), matches.toString());
    }


    /**
     * Метод позволяет получить объект класса {@link Matches} по ID матча
     *
     * @param matchId ID матча, который нужно получить
     * @return Объект класса {@link Matches}, соответствующий запрашиваемому ID, либо <code>null</code>, если матч не
     * был найден
     */
    public Matches getMatchById(int matchId) {
        for (Matches match : matches) {
            if (match.getMatchId() == matchId) {
                return match;
            }
        }
        return null;
    }


    /**
     * Метод позволяет изменить матч в списке матчей турнира
     *
     * @param matchToEdit объект класса {@link Matches}, содержащий информацию об измененном матче
     */
    public void editMatchList(Matches matchToEdit) {
        if (matchToEdit == null)
            return;

        for (Matches match : matches) {
            if (match.getMatchId().equals(matchToEdit.getMatchId())) {
                match = matchToEdit;
            }
        }
    }


    /**
     * Метод позволяет получить все текущие матчи турнира
     *
     * @return Список объектов класса {@link MatchDTO}, содержащих информацию о текущих матчах турнира
     */
    public ArrayList<MatchDTO> getOngoingMatches() {
        ArrayList<MatchDTO> ongoingMatches = new ArrayList<>();

        for (Matches match : matches) {
            if (match.getStatus().equals("playing")) {
                ArrayList<Integer> scoreMap = match.getScoreByMap();

                MatchDTO matchDTO = new MatchDTO(matchesToMatchTimeDTO(match), scoreMap.get(0), scoreMap.get(1));

                ArrayList<Integer> currentMapScore = match.getCurrentScoreOnMap();

                if (!currentMapScore.isEmpty()) {
                    matchDTO.setLeftScore(currentMapScore.get(0).toString());
                    matchDTO.setRightScore(currentMapScore.get(1).toString());
                } else {
                    matchDTO.setLeftScore("-");
                    matchDTO.setRightScore("-");
                }

                ongoingMatches.add(matchDTO);
            }
        }

        return ongoingMatches;
    }


    /**
     * Метод позволяет получить все будущие матчи турнира
     *
     * @return Список объектов класса {@link MatchTimeDTO}, содержащих информацию о будущих матчах турнира
     */
    public ArrayList<MatchTimeDTO> getUpcomingMatches() {
        ArrayList<MatchTimeDTO> upcomingMatches = new ArrayList<>();

        for (Matches match : matches) {
            LocalDateTime matchDate = match.getMatchDate();
            LocalDateTime now = LocalDateTime.now();

            if (matchDate.toLocalDate().isAfter(now.toLocalDate()) || (matchDate.toLocalTime().isAfter(now.toLocalTime()) && matchDate.toLocalDate().isEqual(now.toLocalDate()))) {
                upcomingMatches.add(matchesToMatchTimeDTO(match));
            }
        }

        return upcomingMatches;
    }


    /**
     * Метод позволяет получить занятое место команды или игрока на турнире
     *
     * @param player   ник игрока, чье место на турнире необходимо получить. Не является пустой строкой, если турнир
     *                 в формате 1x1
     * @param teamName название команды, чье место на турнире необходимо получить
     * @return Занятое командой или игроком место на турнире. Может быть пустой строкой, если среди участников турнира
     * не было найдено интересующих команды или игрока
     */
    public String getPlace(String teamName, String player) {
        for (PrizePlaces prizePlace : prizePlaces) {
            if (prizePlace.getTeamName() != null && !prizePlace.getTeamName().isEmpty() &&
                    (prizePlace.getTeamName().equals(teamName) || prizePlace.getTeamName().equals(player))) {
                return prizePlace.getPlace();
            }
        }

        return "";
    }


    /**
     * Метод позволяет получить статус турнира:
     * <li><b>ended</b> - завершенный</li>
     * <li><b>upcoming</b> - будущий</li>
     * <li><b>ongoing</b> - текущий</li>
     *
     * @return Статус турнира
     */
    public String getEventStatus() {
        LocalDate now = LocalDate.now();
        if (dateEnd.isBefore(now))
            return "ended";

        if (dateStart.isAfter(now))
            return "upcoming";

        return "ongoing";
    }


    /**
     * Метод позволяет получить список отсортированных по дате будущих матчей
     *
     * @return Список объектов класса {@link MatchTimeByDateDTO}, содержащих информацию о будущих матчах,
     * соответствующих определенной дате
     */
    public ArrayList<MatchTimeByDateDTO> getSortedByDateUpcomingMatches() {
        ArrayList<MatchTimeDTO> upcomingMatches = new ArrayList<>();

        if (!dateEnd.isBefore(LocalDate.now())) {
            upcomingMatches.addAll(getUpcomingMatches());
        }

        return sortMatchesByDateTime(upcomingMatches, "notreversed");
    }


    /**
     * Метод позволяет получить список отсортированных по времени текущих матчей
     *
     * @return Список объектов класса {@link MatchDTO}, содержащих информацию о текущих матчах, отсортированных по
     * времени
     */
    public ArrayList<MatchDTO> getSortedByTimeOngoingMatches() {
        ArrayList<MatchDTO> ongoingMatches = new ArrayList<>();

        if (!dateEnd.isBefore(LocalDate.now())) {
            ongoingMatches.addAll(getOngoingMatches());
        }

        ongoingMatches.sort(Comparator.comparingInt(o -> LocalTime.parse(o.getTime()).toSecondOfDay()));

        return ongoingMatches;
    }


    /**
     * Метод позволяет получить тип участников турнира:
     * <li><b>player</b> - игрок, если формат турнира 1x1</li>
     * <li><b>team</b> - команда, если формат турнира 2x2 или 5x5</li>
     *
     * @return Тип участников турнира
     */
    public String getParticipantType() {
        if (format.equals("1x1"))
            return "player";
        return "team";
    }


    /**
     * Метод позволяет определить является ли команда или игрок участником турнира
     *
     * @param teamName название команды, которую требуется проверить на участие в турнире
     * @param player   ник игрока, которого требуется проверить на участие в турнире
     * @return Является ли запрашиваемые команда или игрок участниками турнира: <code>true</code>, если да;
     * <code>false</code> иначе
     */
    public boolean isEventParticipant(String teamName, String player) {
        for (Requests request : requests) {
            if (request.getTeamName().equals(teamName) || request.getTeamName().equals(player))
                return true;
        }

        return false;
    }


    /**
     * Метод конвертирует объект класса {@link Matches} в объект класса {@link MatchTimeDTO}
     *
     * @param match объект класса {@link Matches}, содержащий информацию о матче, который необходимо конвертировать в
     *              объект класса {@link MatchTimeDTO}
     * @return Объект класса {@link MatchTimeDTO}, содержащий информацию о матче
     */
    private MatchTimeDTO matchesToMatchTimeDTO(Matches match) {
        return new MatchTimeDTO(
                new EventMatchDTO(
                        match.getMatchId(),
                        parseMatchDate(match.getMatchDate().toLocalDate()),
                        match.getNameFirst(),
                        match.getTagFirst(),
                        match.getNameSecond(),
                        match.getTagSecond(),
                        "-",
                        "-"
                ),
                parseMatchTime(match.getMatchDate()),
                name,
                Integer.toString(match.getTier()),
                match.getMaps());
    }
}
