package com.example.kast.utils;


import com.example.kast.mongo_collections.documents.MapPoolDoc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.kast.utils.Utils.parseMatchDate;


/**
 * Данный класс является утилитой, в которую вынесены часто используемые методы при взаимодействии с турнирами
 *
 * @author Кирилл "Tamada" Симовин
 */
public class EventUtils {
    /**
     * Метод конвертирует список объектов класса {@link MapPoolDoc} в список названий карт
     *
     * @param mapPoolDocs список объектов класса {@link MapPoolDoc}, содержащий информацию о карте, которые необходимо
     *                    конвертировать в список названий карт
     * @return Список названий карт
     */
    public static ArrayList<String> getMapPoolArray(List<MapPoolDoc> mapPoolDocs) {
        ArrayList<String> mapPool = new ArrayList<>();

        for (MapPoolDoc mapPoolDoc : mapPoolDocs) {
            mapPool.add(mapPoolDoc.getName());
        }

        return mapPool;
    }


    /**
     * Метод позволяет перевести дату проведения турнира в дату формата <i>дд.мм.гггг - дд.мм.гггг</i>
     *
     * @param startDate дата начала турнира
     * @param endDate   дата окончания турнира
     * @return Строка формата дд.мм.гггг - дд.мм.гггг
     */
    public static String parseEventDate(LocalDate startDate, LocalDate endDate) {
        return String.format("%s - %s", parseMatchDate(startDate), parseMatchDate(endDate));
    }
}
