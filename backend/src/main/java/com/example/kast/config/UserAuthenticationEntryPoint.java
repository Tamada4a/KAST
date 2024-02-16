package com.example.kast.config;


import com.example.kast.dto.ErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Данный класс является стартовой точкой входа при аутентификации
 *
 * @author Кирилл "Tamada" Симовин
 */
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Объект класса {@link ObjectMapper} для записи ошибки в виде JSON
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    /**
     * В случае попытки доступа неавторизованным пользователем, будет получена ошибка с сообщением
     * "Не авторизован"
     *
     * @throws IOException при неавторизованном доступе
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        OBJECT_MAPPER.writeValue(response.getOutputStream(), new ErrorDTO("Не авторизован"));
    }
}
