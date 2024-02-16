package com.example.kast.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.services.AuthService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;


/**
 * В данном классе реализуются методы создания и валидации токена
 *
 * @author Кирилл "Tamada" Симовин
 */
@Service
@RequiredArgsConstructor
public class UserAuthProvider {
    /**
     * Задаем ключу значение "secret-value"
     */
    @Value("${security.jwt.token.secret-key:secret-value}")
    private String secretKey;

    /**
     * Объект класса {@link AuthService} - сервис, обрабатывающий запросы аутентификации
     */
    private final AuthService authService;


    /**
     * Метод инициализирует секретный ключ, кодируя его изначальное значение
     */
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    /**
     * Метод создает JWT-токен
     *
     * @param player       ник аутентифицировавшегося пользователя
     * @param isRememberMe значение поля "запомнить меня"
     * @return Новый JWT-токен. В зависимости от значения <code>isRememberMe</code> устанавливается время жизни токена
     */
    public String createToken(String player, Boolean isRememberMe) {
        Date now = new Date();
        if (isRememberMe)
            return JWT.create()
                    .withIssuer(player)
                    .withIssuedAt(now)
                    .withExpiresAt(new Date(now.getTime() + 720L * 3_600_000))
                    .sign(Algorithm.HMAC256(secretKey));

        return JWT.create()
                .withIssuer(player)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + 3_600_000))
                .sign(Algorithm.HMAC256(secretKey));
    }


    /**
     * Метод валидирует токен
     *
     * @param token JWT-токен, находящийся в заголовке <code>AUTHORIZATION</code>
     * @return объект класса {@link Authentication}, содержащий информацию о пользователе, совершившем запрос
     * @throws AppException Если пользователя с таким ником не существует в базе данных
     */
    public Authentication validateToken(String token) throws AppException {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secretKey)).build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        PlayerDoc player = authService.findByNick(decodedJWT.getIssuer());

        return new UsernamePasswordAuthenticationToken(player, null, Collections.emptyList());
    }
}
