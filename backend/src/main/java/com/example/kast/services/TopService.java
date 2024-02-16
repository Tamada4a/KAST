package com.example.kast.services;


import com.example.kast.controllers.dto.top.FullTopDTO;
import com.example.kast.controllers.dto.top.TopGetDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.CountryDoc;
import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.documents.TeamDoc;
import com.example.kast.mongo_collections.documents.TopDoc;
import com.example.kast.mongo_collections.interfaces.CountryRepository;
import com.example.kast.mongo_collections.interfaces.PlayerRepository;
import com.example.kast.mongo_collections.interfaces.TeamRepository;
import com.example.kast.mongo_collections.interfaces.TopRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.kast.utils.Utils.parseMatchDate;


/**
 * Данный класс является сервисом, реализующим логику обработки запросов, связанных со страницей топа
 *
 * @param teamRepository    интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @param playerRepository  интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param countryRepository интерфейс для взаимодействия с сущностями {@link CountryDoc}
 * @param topRepository     интерфейс для взаимодействия с сущностями {@link TopDoc}
 * @param teamService       объект класса {@link TeamService} - сервис, обрабатывающий запросы, приходящие со страницы
 *                          команды
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record TopService(TeamRepository teamRepository, PlayerRepository playerRepository,
                         CountryRepository countryRepository, TopRepository topRepository,
                         TeamService teamService) {
    /**
     * Метод позволяет получить информацию о топе команд и последней дате его обновления<br></br>
     * Если в базе данных нет информации о дате обновления топа, будет использована текущая дата
     *
     * @return Объект класса {@link FullTopDTO}, содержащий информацию о топе команд и дате его обновления
     */
    public FullTopDTO getFullTop() {
        ArrayList<TopGetDTO> topTeams = getTopTeams(sortTeamsByTop());

        List<TopDoc> topDocList = topRepository.findAll();

        String topDate = parseMatchDate(LocalDate.now());
        if (!topDocList.isEmpty())
            topDate = topDocList.get(0).getTopDate();

        return new FullTopDTO(topTeams, topDate);
    }


    /**
     * Метод позволяет изменить положение команд в топе
     *
     * @param topSetDTO объект класса {@link FullTopDTO}, содержащий информацию об измененном положении команд в топе
     * @return Объект класса {@link FullTopDTO}, содержащий информацию об измененном топе команд и дате его обновления
     * @throws AppException Если команды с таким названием не существует в базе данных
     */
    public FullTopDTO setTop(FullTopDTO topSetDTO) throws AppException {
        topRepository.deleteAll();
        topRepository.save(new TopDoc(topSetDTO.getTopDate()));

        List<TopGetDTO> teams = topSetDTO.getTopTeams();

        for (TopGetDTO team : teams) {
            if (!teamRepository.existsByTeamName(team.getName()))
                throw new AppException("Неизвестная команда", HttpStatus.NOT_FOUND);

            TeamDoc teamDoc = teamRepository.findByTeamName(team.getName());

            teamDoc.setTop(team.getTopPosition());
            teamDoc.setTopDiff(team.getChangedPosition());

            teamRepository.save(teamDoc);
        }

        return topSetDTO;
    }


    /**
     * Метод позволяет получить информацию о каждой команде, находящейся в топе: название, позицию в топе, изменение
     * позиции команды в топе, состав команды
     *
     * @param teams список объектов класса {@link TeamDoc}, содержащий информацию о каждой команде
     * @return Список объектов класса {@link TopGetDTO}, содержащих основную информацию о всех командах, необходимую
     * для отображения
     */
    private ArrayList<TopGetDTO> getTopTeams(List<TeamDoc> teams) {
        ArrayList<TopGetDTO> topTeams = new ArrayList<>();

        for (TeamDoc team : teams) {
            topTeams.add(new TopGetDTO(
                    team.getTeamName(),
                    team.getTop(),
                    team.getTopDiff(),
                    teamService.getTeamPlayers(team.getTeamName()))
            );
        }

        return topTeams;
    }


    /**
     * Метод сортирует все команды, хранящиеся в базе данных, по позициям в топе
     *
     * @return Список объектов класса {@link TeamDoc}, содержащих информацию всех командах
     */
    private List<TeamDoc> sortTeamsByTop() {
        List<TeamDoc> teamDocList = teamRepository.findAll();

        ArrayList<TeamDoc> posTeamTop = new ArrayList<>();
        ArrayList<TeamDoc> negTeamTop = new ArrayList<>();

        for (TeamDoc team : teamDocList) {
            if (team.getTop() > 0) {
                posTeamTop.add(team);
            } else {
                negTeamTop.add(team);
            }
        }

        posTeamTop.sort(Comparator.comparingInt(TeamDoc::getTop));
        posTeamTop.addAll(negTeamTop);

        return posTeamTop;
    }
}
