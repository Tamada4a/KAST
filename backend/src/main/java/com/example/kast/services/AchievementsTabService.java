package com.example.kast.services;


import com.example.kast.controllers.dto.tab.EventInfoDTO;
import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.mongo_collections.embedded.Rosters;
import com.example.kast.mongo_collections.interfaces.PlayerRepository;
import com.example.kast.mongo_collections.interfaces.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.kast.utils.Utils.parseMatchDate;
import static com.example.kast.utils.Utils.parseStringDateToEpochDays;


/**
 * Данный класс является сервисом, реализующим логику получения достижений пользователя или команды
 *
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record AchievementsTabService(PlayerRepository playerRepository, TournamentRepository tournamentRepository) {
    /**
     * Метод позволяет получить список всех достижений игрока
     *
     * @param player ник игрока, чьи достижения необходимо получить
     * @param type   тип турнира: Lan или Online
     * @return Список объектов класса {@link EventInfoDTO}, содержащих информацию о призовых местах пользователя на
     * турнирах соответствующего типа, отсортированных по дате проведения турниров
     */
    public ArrayList<EventInfoDTO> getPlayerAchievements(String player, String type) {

        PlayerDoc playerDoc = playerRepository.findByNick(player);

        ArrayList<EventInfoDTO> achievementsList = new ArrayList<>();

        List<TournamentDoc> tournamentDocs = tournamentRepository.findAll();

        for (TournamentDoc tournamentDoc : tournamentDocs) {
            if (!tournamentDoc.getType().equals(type))
                continue;

            ArrayList<Rosters> rosters = playerDoc.getRosters();
            for (Rosters roster : rosters) {
                String place = tournamentDoc.getPlace(roster.getTeamName(), player);
                if (place.isEmpty() || !isPrizePlaces(place.split("-")))
                    continue;

                String dateStartStr = parseMatchDate(tournamentDoc.getDateStart());
                String dateEndStr = parseMatchDate(tournamentDoc.getDateEnd());

                EventInfoDTO eventInfoDTO = new EventInfoDTO(tournamentDoc.getName(), place,
                        dateStartStr + " - " + dateEndStr);
                if (!achievementsList.contains(eventInfoDTO))
                    achievementsList.add(eventInfoDTO);
            }
        }

        if (!achievementsList.isEmpty()) {
            achievementsList.sort(Comparator.comparingInt(o -> parseStringDateToEpochDays(o.getDate().substring(0, 10))));
            Collections.reverse(achievementsList);
        }

        return achievementsList;
    }


    /**
     * Метод позволяет получить список всех достижений игрока
     *
     * @param teamName название команды, чьи достижения необходимо получить
     * @param type     тип турнира: Lan или Online
     * @return Список объектов класса {@link EventInfoDTO}, содержащих информацию о призовых местах пользователя на
     * турнирах соответствующего типа, отсортированных по дате проведения турниров
     */
    public ArrayList<EventInfoDTO> getTeamAchievements(String teamName, String type) {

        ArrayList<EventInfoDTO> achievementsList = new ArrayList<>();

        List<TournamentDoc> tournaments = tournamentRepository.findAll();

        for (TournamentDoc tournament : tournaments) {
            String eventName = tournament.getName();

            if (!tournament.getType().equals(type))
                continue;

            String dateStart = parseMatchDate(tournament.getDateStart());
            String dateEnd = parseMatchDate(tournament.getDateEnd());

            String place = tournament.getPlace(teamName, "");
            if (!place.isEmpty() && isPrizePlaces(place.split("-"))) {
                achievementsList.add(new EventInfoDTO(eventName, place, dateStart + " - " + dateEnd));
            }
        }

        if (!achievementsList.isEmpty()) {
            achievementsList.sort(Comparator.comparingInt(o -> parseStringDateToEpochDays(o.getDate().substring(0, 10))));
            Collections.reverse(achievementsList);
        }

        return achievementsList;
    }


    /**
     * Метод позволяет определить, является ли занятое командой или пользователем место призовым
     *
     * @param prizePlaces массив, состоящий из занятых мест. Массив состоит из одного или двух элементов: <br></br>
     *                    В случае, если место обобщенное, например, <i>5-8 место</i> - массив состоит из двух элементов (5, 8);
     *                    <br></br>
     *                    Если место конкретное, например, <i>1 место</i>, массив содержит один элемент (1)
     * @return Является ли занятое командой или пользователем место призовым: <code>true</code>, если является;
     * <code>false</code> иначе
     */
    private boolean isPrizePlaces(String[] prizePlaces) {
        for (String prizePlace : prizePlaces) {
            if (!(prizePlace.equals("1") || prizePlace.equals("2") || prizePlace.equals("3") || prizePlace.equals("4")))
                return false;
        }
        return true;
    }
}
