package com.example.kast.changelogs;


import com.example.kast.mongo_collections.documents.AdminDoc;
import com.example.kast.mongo_collections.interfaces.AdminRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Данный класс содержит логику для миграции списка объектов класса {@link AdminDoc}. Используется для инициализации
 * соответствующей коллекции базы данных
 *
 * @author Кирилл "Tamada" Симовин
 */
@ChangeUnit(id = "AdminsChangeLog", order = "30001", author = "Tamada")
public class AdminsChangeLog {
    /**
     * Метод позволяет сохранить полученный список объектов класса {@link AdminDoc}, содержащих ники администраторов
     *
     * @param adminRepository интерфейс для взаимодействия с сущностями {@link AdminDoc}
     */
    @Execution
    public void changeSet(AdminRepository adminRepository) {
        getAdmins().forEach(adminRepository::save);
    }


    /**
     * Метод позволяет откатить внесенные изменения
     */
    @RollbackExecution
    public void rollback() {
    }


    /**
     * Метод позволяет получить список ников всех администраторов, которых необходимо добавить в базу данных
     *
     * @return Список объектов класса {@link AdminDoc}, содержащих ники администраторов сервиса
     */
    private ArrayList<AdminDoc> getAdmins() {
        // Укажите здесь ники администраторов
        return new ArrayList<>(Arrays.asList());
    }
}
