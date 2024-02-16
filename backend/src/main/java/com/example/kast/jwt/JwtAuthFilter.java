package com.example.kast.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * Данный класс отвечает за проверку токена на валидность, если в запросе есть заголовок <code>AUTHORIZATION</code>.
 * Является расширением {@link OncePerRequestFilter}
 *
 * @author Кирилл "Tamada" Симовин
 */
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    /**
     * Объект класса {@link UserAuthProvider} - сервис, валидирующий токен
     */
    private final UserAuthProvider userAuthProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
//TODO: Если чел не админ и делает set, то блокать его

//        System.out.println(request.getRequestURL().toString());
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null) {
            String[] elements = header.split(" ");

            if (elements.length == 2 && "Bearer".equals(elements[0])) {
                try {
                    SecurityContextHolder.getContext().setAuthentication(
                            userAuthProvider.validateToken(elements[1])
                    );
                } catch (RuntimeException e) {
                    SecurityContextHolder.clearContext();
                    throw e;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
