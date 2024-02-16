package com.example.kast.controllers;


import com.example.kast.controllers.dto.auth.CredentialsDTO;
import com.example.kast.controllers.dto.auth.RegistrationDTO;
import com.example.kast.controllers.dto.auth.UserDTO;
import com.example.kast.exceptions.AppException;
import com.example.kast.jwt.UserAuthProvider;
import com.example.kast.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;


/**
 * Данный REST-контроллер отвечает за обработку запросов на логин и регистрацию
 *
 * @param authService      объект класса {@link AuthService} - сервис, обрабатывающий запросы аутентификации
 * @param userAuthProvider объект класса {@link UserAuthProvider} - позволяет создавать и валидировать токен
 * @author Кирилл "Tamada" Симовин
 */
@RequestMapping("/auth")
@RestController
public record AuthController(AuthService authService,
                             UserAuthProvider userAuthProvider) {
    /**
     * Метод обрабатывает POST-запрос по пути "/auth/login", то есть вызывается при логине пользователя
     *
     * @param credentialsDTO объект класса {@link CredentialsDTO}, содержащий ник, пароль и состояние кнопки "Запомнить меня"
     * @return <code>ResponseEntity</code> со статусом 200 и объектом класса {@link UserDTO}, содержащего информацию о пользователе, в качестве тела
     * @throws AppException В случае, если пользователя с введенным ником не существует или пароль введен неверно
     */
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody CredentialsDTO credentialsDTO) throws AppException {
        UserDTO userDTO = authService.login(credentialsDTO);
        userDTO.setToken(userAuthProvider.createToken(userDTO.getNick(), credentialsDTO.getIsRememberMe()));

        return ResponseEntity.ok(userDTO);
    }


    /**
     * Метод обрабатывает POST-запрос по пути "/auth/register", то есть вызывается при логине пользователя
     *
     * @param registrationDTO объект класса {@link RegistrationDTO}, содержащий данные пользователя для регистрации
     * @return <code>ResponseEntity</code> со статусом 201 и объектом класса {@link UserDTO}, содержащего информацию о
     * зарегистрированном пользователе, в качестве тела
     * @throws AppException В случае, если пользователь пытается зарегистрировать существующий ник
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegistrationDTO registrationDTO) throws AppException {
        UserDTO userDTO = authService.register(registrationDTO);
        userDTO.setToken(userAuthProvider.createToken(userDTO.getNick(), false));

        return ResponseEntity.created(URI.create("/" + userDTO.getNick())).body(userDTO);
    }
}
