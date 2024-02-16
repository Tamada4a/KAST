package com.example.kast.mongo_collections.embedded;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит информацию о занятом месте и полученном вознаграждении соответствующей команды
 *
 * @author Кирилл "Tamada" Симовин, Александр "ugly4" Федякин
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrizePlaces {
    /**
     * Название команды-участницы (может быть как ник игрока (в случае турнира 1x1), так и название команды)
     */
    private String teamName;

    /**
     * Занятое место. Может быть пустой строкой, если место ещё не определено
     */
    private String place;

    /**
     * Полученное вознаграждение. Может быть пустой строкой, если место ещё не определено
     */
    private String reward;
}
