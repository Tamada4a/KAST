package com.example.kast.services;


import com.example.kast.controllers.dto.auth.CredentialsDTO;
import com.example.kast.controllers.dto.auth.RegistrationDTO;
import com.example.kast.controllers.dto.auth.UserDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.interfaces.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;


/**
 * Данный класс является сервисом, реализующим логику логина и регистрации
 *
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param passwordEncoder      интерфейс для кодирования паролей
 * @param utilsService         объект класса {@link UtilsService} - сервис, содержащий часто используемые методы без
 *                             привязки к конкретному сервису
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record AuthService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder, UtilsService utilsService) {
    /**
     * Метод сверяет пароль, введенный в форме, с паролем пользователя в базе данных
     *
     * @param credentialsDTO объект класса {@link CredentialsDTO}, содержащий информацию из формы логина
     * @return Объект класса {@link UserDTO}, содержащий информацию об авторизованном пользователе
     * @throws AppException В случае, если пользователя с введенным ником не существует или пароль введен неверно
     */
    public UserDTO login(CredentialsDTO credentialsDTO) throws AppException {
        if (!playerRepository.existsByNick(credentialsDTO.getNick()))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        PlayerDoc player = playerRepository.findByNick(credentialsDTO.getNick());

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDTO.getPassword()), player.getPassword()))
            return playerDocToUserDto(player);
        throw new AppException("Неправильный пароль", HttpStatus.BAD_REQUEST);
    }


    /**
     * Метод добавляет в базу данных нового пользователя, если с таким ником никого не зарегистрировано
     *
     * @param registrationDTO объект класса {@link RegistrationDTO}, содержащий информацию о новом пользователе из формы
     *                        регистрации
     * @return Объект класса {@link UserDTO}, содержащий информацию об авторизованном пользователе
     * @throws AppException В случае, если пользователь пытается зарегистрировать существующий ник
     */
    public UserDTO register(RegistrationDTO registrationDTO) throws AppException {
        if (utilsService.isAlreadyExists(registrationDTO.getNick()) || registrationDTO.getNick().equals("NonPhoto"))
            throw new AppException("Пользователь с таким ником уже существует", HttpStatus.BAD_REQUEST);


        PlayerDoc playerDoc = registrationDtoToPlayerDoc(registrationDTO);
        playerDoc.setPassword(passwordEncoder.encode(CharBuffer.wrap(registrationDTO.getPassword())));

        PlayerDoc savedPlayer = playerRepository.save(playerDoc);

        return playerDocToUserDto(savedPlayer);
    }


    /**
     * Метод используется для поиска объекта класса {@link PlayerDoc} с соответствующим ником в базе данных
     *
     * @param nick ник пользователя, информацию о котором необходимо найти
     * @return Объект класса {@link PlayerDoc}, содержащий информацию об интересующем пользователе
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    public PlayerDoc findByNick(String nick) throws AppException {
        if (!playerRepository.existsByNick(nick) && !playerRepository.existsByOldNick(nick))
            throw new AppException("Неизвестный пользователь", HttpStatus.NOT_FOUND);

        if (playerRepository.existsByOldNick(nick)) {
            return playerRepository.findByOldNick(nick);
        }
        return playerRepository.findByNick(nick);
    }


    /**
     * Метод конвертирует объект класса {@link PlayerDoc} в объект класса {@link UserDTO}
     *
     * @param playerDoc объект класса {@link PlayerDoc}, содержащий информацию о пользователе, который необходимо
     *                  конвертировать в объект класса {@link UserDTO}
     * @return Объект класса {@link UserDTO}, содержащий информацию о пользователе
     */
    private UserDTO playerDocToUserDto(PlayerDoc playerDoc) {
        return new UserDTO(playerDoc.getFirstName(), playerDoc.getSecondName(),
                playerDoc.getEmail(), playerDoc.getCountry(), playerDoc.getNick(), "");
    }


    /**
     * Метод конвертирует объект класса {@link RegistrationDTO} в объект класса {@link PlayerDoc}
     *
     * @param registrationDTO объект класса {@link RegistrationDTO}, содержащий информацию о новом пользователе, который
     *                        необходимо конвертировать в объект класса {@link PlayerDoc}
     * @return Объект класса {@link PlayerDoc}, содержащий информацию о пользователе
     */
    private PlayerDoc registrationDtoToPlayerDoc(RegistrationDTO registrationDTO) {
        return new PlayerDoc(playerRepository.findAll().size() + 1, registrationDTO.getNick(),
                registrationDTO.getFirstName(), registrationDTO.getLastName(),
                registrationDTO.getCountry(), registrationDTO.getEmail());
    }
}
