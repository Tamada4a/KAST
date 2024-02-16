package com.example.kast.services;


import com.example.kast.controllers.dto.other.NameDTO;
import com.example.kast.controllers.dto.other.NotificationsDTO;
import com.example.kast.controllers.dto.other.SearchDataDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.*;
import com.example.kast.mongo_collections.interfaces.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Данный класс является сервисом, реализующим логику обработки всех запросов без привязки к конкретной странице
 *
 * @param countryRepository    интерфейс для взаимодействия с сущностями {@link CountryDoc}
 * @param adminRepository      интерфейс для взаимодействия с сущностями {@link AdminDoc}
 * @param teamRepository       интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record OtherService(CountryRepository countryRepository, AdminRepository adminRepository,
                           TeamRepository teamRepository, PlayerRepository playerRepository,
                           TournamentRepository tournamentRepository) {
    /**
     * Метод позволяет получить список всех стран
     *
     * @return Список объектов класса {@link CountryDoc}, содержащих информацию о всех странах, находящихся в базе даннах
     */
    public List<CountryDoc> getAllCountries() {
        return countryRepository.findAll();
    }


    /**
     * Метод позволяет установить, является ли пользователь администратором
     *
     * @param id ник пользователя, для которого проверяется, является ли он администратором
     * @return Является ли пользователь администратором: <code>true</code>, если является; <code>false</code> иначе
     */
    public Boolean isAdmin(String id) {
        return adminRepository.existsByAdminId(id);
    }


    /**
     * Метод позволяет получить данные, среди которых будет осуществляться поиск в поисковой строке, расположенной в
     * заголовке страницы
     *
     * @return Объект класса {@link SearchDataDTO}, содержащий данные, среди которых осуществляется поиск в поисковой
     * строке, расположенной в заголовке страницы
     */
    public SearchDataDTO getSearchData() {
        List<TournamentDoc> tournamentDocList = tournamentRepository.findAll();
        List<TeamDoc> teamDocList = teamRepository.findAll();
        List<PlayerDoc> playerDocList = playerRepository.findAll();

        ArrayList<NameDTO> events = new ArrayList<>();
        ArrayList<NameDTO> teams = new ArrayList<>();
        ArrayList<NameDTO> players = new ArrayList<>();

        for (TournamentDoc tournamentDoc : tournamentDocList) {
            events.add(new NameDTO(tournamentDoc.getName()));
        }

        for (TeamDoc teamDoc : teamDocList) {
            teams.add(new NameDTO(teamDoc.getTeamName()));
        }

        for (PlayerDoc playerDoc : playerDocList) {
            players.add(new NameDTO(playerDoc.getNick()));
        }

        return new SearchDataDTO(events, teams, players);
    }


    /**
     * Метод позволяет получить все уведомления пользователя
     *
     * @param player ник игрока, чьи уведомления необходимо получить
     * @return Список объектов класса {@link NotificationsDTO}, содержащих информацию об уведомлениях пользователя
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    public ArrayList<NotificationsDTO> getNotifications(String player) throws AppException {
        if (!playerRepository.existsByNick(player))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        PlayerDoc playerDoc = playerRepository.findByNick(player);
        ArrayList<NotificationsDTO> notificationsToSetList = new ArrayList<>(playerDoc.getNotifications());

        ArrayList<NotificationsDTO> notificationsList = new ArrayList<>(playerDoc.getNotifications());

        notificationsToSetList.removeIf(notification -> !notification.getDescription().contains("Вас пригласили в команду"));

        playerDoc.setNotifications(notificationsToSetList);

        playerRepository.save(playerDoc);

        return notificationsList;
    }
}
