package com.example.kast.services;

import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.documents.TeamDoc;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.mongo_collections.interfaces.PlayerRepository;
import com.example.kast.mongo_collections.interfaces.TeamRepository;
import com.example.kast.mongo_collections.interfaces.TournamentRepository;
import org.springframework.stereotype.Service;


/**
 * Данный класс является сервисом-утилитой, содержащим методы без привязки к конкретному сервису
 *
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @param teamRepository       интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record UtilsService(PlayerRepository playerRepository, TournamentRepository tournamentRepository,
                           TeamRepository teamRepository) {
    /**
     * Метод позволяет определить, есть ли в базе данных объект с интересующим названием
     *
     * @param name название, которое необходимо проверить на наличие в базе данных
     * @return Есть ли в базе данных объект с интересующим названием: <code>true</code>, если является; <code>false</code>
     * иначе
     */
    public boolean isAlreadyExists(String name) {
        return playerRepository.existsByNick(name) || tournamentRepository.existsByName(name) ||
                teamRepository.existsByTeamName(name) || teamRepository.existsByTag(name);
    }
}
