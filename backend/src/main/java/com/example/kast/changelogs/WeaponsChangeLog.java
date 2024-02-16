package com.example.kast.changelogs;


import com.example.kast.mongo_collections.documents.WeaponDoc;
import com.example.kast.mongo_collections.interfaces.WeaponRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Данный класс содержит логику для миграции списка объектов класса {@link WeaponDoc}. Используется для инициализации
 * соответствующей коллекции базы данных
 *
 * @author Кирилл "Tamada" Симовин
 */
@ChangeUnit(id = "WeaponsInitChangeLog", order = "10001", author = "Tamada")
public class WeaponsChangeLog {
    /**
     * Метод позволяет сохранить полученный список объектов класса {@link WeaponDoc}, содержащих информацию обо всех
     * оружиях, доступных в текущей версии CS
     *
     * @param weaponRepository интерфейс для взаимодействия с сущностями {@link WeaponDoc}
     */
    @Execution
    public void changeSet(WeaponRepository weaponRepository) {
        getWeapons()
                .forEach(weaponRepository::save);
    }


    /**
     * Метод позволяет откатить внесенные изменения
     */
    @RollbackExecution
    public void rollback() {
    }


    /**
     * Метод позволяет получить список всех пар <i>оружие - тип_оружия</i>.<br></br><br></br>
     * Типы оружия:
     * <li><b>pistol</b> - пистолет</li>
     * <li><b>other</b> - все остальное</li>
     *
     * @return Список объектов класса {@link WeaponDoc}, содержащих информацию обо всех оружиях, доступных в текущей
     * версии CS
     */
    private ArrayList<WeaponDoc> getWeapons() {
        return new ArrayList<WeaponDoc>(Arrays.asList(
                // пистолеты
                new WeaponDoc("usp_silencer", "pistol"),
                new WeaponDoc("usp_silencer_off", "pistol"),
                new WeaponDoc("hkp2000", "pistol"),
                new WeaponDoc("elite", "pistol"),
                new WeaponDoc("p250", "pistol"),
                new WeaponDoc("fiveseven", "pistol"),
                new WeaponDoc("deagle", "pistol"),
                new WeaponDoc("glock", "pistol"),
                new WeaponDoc("tec9", "pistol"),
                new WeaponDoc("cz75a", "pistol"),
                new WeaponDoc("revolver", "pistol"),

                // пистолеты-пулеметы
                new WeaponDoc("mp7", "other"),
                new WeaponDoc("mac10", "other"),
                new WeaponDoc("mp9", "other"),
                new WeaponDoc("ump45", "other"),
                new WeaponDoc("p90", "other"),
                new WeaponDoc("bizon", "other"),
                new WeaponDoc("mp5sd", "other"),

                // дробовики
                new WeaponDoc("nova", "other"),
                new WeaponDoc("xm1014", "other"),
                new WeaponDoc("mag7", "other"),
                new WeaponDoc("sawedoff", "other"),

                // пулеметы
                new WeaponDoc("m249", "other"),
                new WeaponDoc("negev", "other"),

                // штурмовые винтовки
                new WeaponDoc("famas", "other"),
                new WeaponDoc("m4a1", "other"),
                new WeaponDoc("m4a1_silencer", "other"),
                new WeaponDoc("m4a1_silencer_off", "other"),
                new WeaponDoc("ssg08", "other"),
                new WeaponDoc("aug", "other"),
                new WeaponDoc("awp", "other"),
                new WeaponDoc("scar20", "other"),
                new WeaponDoc("galilar", "other"),
                new WeaponDoc("ak47", "other"),
                new WeaponDoc("sg556", "other"),
                new WeaponDoc("g3sg1", "other")
        ));
    }
}
