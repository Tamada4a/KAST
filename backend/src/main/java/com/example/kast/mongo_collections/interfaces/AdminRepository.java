package com.example.kast.mongo_collections.interfaces;


import com.example.kast.mongo_collections.documents.AdminDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Интерфейс репозитория, работающий с сущностями {@link AdminDoc}. Является расширением {@link MongoRepository}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Repository
public interface AdminRepository extends MongoRepository<AdminDoc, String> {
    /**
     * Метод позволяет удалить администратора из базы данных
     *
     * @param adminId ник пользователя, которого необходимо удалить из списка администраторов
     */
    void deleteByAdminId(final String adminId);


    /**
     * Метод позволяет определить, является ли пользователь администратором
     *
     * @param adminId ник пользователя, которого нужно проверить на наличие прав администратора
     * @return Является ли пользователь администратором: <code>true</code>, если является; <code>false</code> иначе
     */
    boolean existsByAdminId(final String adminId);
}
