package com.example.kast.controllers.dto.event;


import com.example.kast.mongo_collections.embedded.PrizePlaces;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Класс является расширением {@link PrizePlaces}. Здесь добавляется единственное поле - <em>team<em/>,
 * соответствующее команде игрока. Класс имеет место в турнирах 1x1, где в призовом месте отображается игрок
 * и логотип его команды за спиной
 *
 * @author Кирилл "Tamada" Симовин
 */
@Getter
@Setter
@NoArgsConstructor
public class EventPrizePlaceDTO extends PrizePlaces {
    /**
     * Название команды игрока, занявшего призовое место. Если турнир проходил не в формате 1x1 - пустая строка
     */
    private String team;


    /**
     * Инициализирует и создает новый объект класса EventPrizePlaceDTO
     *
     * @param teamName название команды-участницы (может быть как ник игрока (в случае турнира 1x1), так и
     *                 название команды)
     * @param place    занятое место. Может быть пустой строкой, если место ещё не определено
     * @param reward   полученное вознаграждение. Может быть пустой строкой, если место ещё не определено
     * @param team     название команды игрока, занявшего призовое место. Если турнир проходил не в формате 1x1 - пустая строка
     */
    public EventPrizePlaceDTO(String teamName, String place, String reward, String team) {
        this.setTeamName(teamName);
        this.setPlace(place);
        this.setReward(reward);
        this.team = team;
    }


    /**
     * Переопределенный метод строкового представления объекта класса {@link EventPrizePlaceDTO}
     *
     * @return Строковое представление объекта класса {@link EventPrizePlaceDTO}
     */
    @Override
    public String toString() {
        return String.format("EventPrizePlaceDTO(teamName=%s, place=%s, reward=%s, team=%s)", this.getTeamName(),
                this.getPlace(), this.getReward(), team);
    }
}
