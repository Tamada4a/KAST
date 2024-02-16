package com.example.kast.utils;


import com.example.kast.controllers.dto.matches.MatchTimeByDateDTO;
import com.example.kast.controllers.dto.tournaments.EndedEventsByDateDTO;
import com.example.kast.controllers.dto.tournaments.FeaturedEventsByDateDTO;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;


/**
 * Данный класс является утилитой, в которую вынесены часто используемые методы без привязки к конкретным сервисам
 *
 * @author Кирилл "Tamada" Симовин
 */
public class Utils {
    /**
     * Матч переводит объект класса {@link LocalDate} в строку формата <i>дд.мм.гггг/i>
     *
     * @param date объект класса {@link LocalDate}, который необходимо перевести в строку
     * @return Строка формата <i>дд.мм.гггг/i>
     */
    public static String parseMatchDate(LocalDate date) {
        return String.format("%s.%s.%s", fixNumber(date.getDayOfMonth()), fixNumber(date.getMonthValue()), date.getYear());
    }


    /**
     * Метод присоединяет "0" к числу, если оно меньше 10.<br></br>
     * Например:<br></br>
     * На вход подается число 9, на выходе получим "09".<br></br>
     * На вход подается число 11 - на выходе получим "11"
     *
     * @param number число, которое необходимо представить в виде строки
     * @return Строковое представление числа
     */
    public static String fixNumber(int number) {
        String str = Integer.toString(number);
        if (number < 10)
            str = "0" + str;
        return str;
    }


    /**
     * Метод позволяет заменить "-" на пробелы
     *
     * @param string строка, в которой необходимо заменить "-" на пробелы
     * @return Входная строка с замененными "-" на пробелы
     */
    public static String replaceDashes(String string) {
        return string.replaceAll("-", " ");
    }


    /**
     * Метод позволяет заменить пробелы на "-"
     *
     * @param string строка, в которой необходимо заменить пробелы на "-"
     * @return Входная строка с замененными пробелами на "-"
     */
    public static String replaceSpaces(String string) {
        return string.replaceAll(" ", "-");
    }


    /**
     * Метод позволяет получить значение по ключу из <i>config.properties</i>
     *
     * @param property свойство, значение которого необходимо получить
     * @return Если произошла ошибка - пустая строка. Иначе значение, соответствующее запрашиваемому свойству
     */
    public static String getProperty(String property) {
        String result = "";
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {

            Properties prop = new Properties();
            prop.load(input);
            result = prop.getProperty(property);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }


    /**
     * Метод переводит строковое представление даты в количество дней, прошедших с <i>01.01.1970</i>
     *
     * @param date дата в формате <i>дд.мм.гггг</i>
     * @return Количество дней, прошедших с <i>01.01.1970</i>
     */
    public static int parseStringDateToEpochDays(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return (int) LocalDate.parse(date, formatter).toEpochDay();
    }


    /**
     * Метод позволяет получить название месяца по его порядковому номеру. Например: <i>1 - Январь</i>
     *
     * @param mIndex порядковый номер месяца
     * @return Название месяца
     */
    public static String getMonthName(int mIndex) {
        return switch (mIndex) {
            case 1 -> "Январь";
            case 2 -> "Февраль";
            case 3 -> "Март";
            case 4 -> "Апрель";
            case 5 -> "Май";
            case 6 -> "Июнь";
            case 7 -> "Июль";
            case 8 -> "Август";
            case 9 -> "Сентябрь";
            case 10 -> "Октябрь";
            case 11 -> "Ноябрь";
            case 12 -> "Декабрь";
            default -> "";
        };
    }


    /**
     * Метод позволяет определить индекс объекта, соответствующий запрашиваемой дате
     *
     * @param objectsList список объектов, распределенных по соответствующим датам, среди которых производится поиск
     *                    искомой даты
     * @param date        дата, которую необходимо найти
     * @param type        тип объектов, среди которых ищется дата:
     *                    <li><b>matches</b> - поиск происходит среди объектов класса {@link MatchTimeByDateDTO},
     *                    информацию о матчах, соответствующих определенной дате. В этом случае искомая дата будет в
     *                    формате <i>дд.мм.гггг</i></li>
     *                    <li><b>endedEvents</b> - поиск происходит среди объектов класса {@link EndedEventsByDateDTO},
     *                    содержащих информацию о прошедших турнирах, соответствующих определенной дате. В этом случае
     *                    искомая дата будет в формате <i>Месяц год</i></li>
     *                    <li><b>featuredEvents</b> - поиск происходит среди объектов класса {@link FeaturedEventsByDateDTO},
     *                    содержащих информацию о будущих турнирах, соответствующих определенной дате. В этом случае
     *                    искомая дата будет в формате <i>Месяц год</i></li>
     * @return Если искомая дата найдена - индекс объекта, соответствующий дате. Иначе -1
     */
    public static int getDateIndexFromList(Object objectsList, String date, String type) {
        ArrayList<Object> castedObjectsList = (ArrayList<Object>) objectsList;
        for (Object object : castedObjectsList) {
            if (switch (type) {
                case "matches" -> ((MatchTimeByDateDTO) object).getDate().equals(date);
                case "endedEvents" -> ((EndedEventsByDateDTO) object).getDate().equals(date);
                case "featuredEvents" -> ((FeaturedEventsByDateDTO) object).getDate().equals(date);
                default -> false;
            }) return castedObjectsList.indexOf(object);
        }
        return -1;
    }
}