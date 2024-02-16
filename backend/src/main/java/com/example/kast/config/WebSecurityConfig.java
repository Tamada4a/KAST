package com.example.kast.config;


import com.example.kast.jwt.JwtAuthFilter;
import com.example.kast.jwt.UserAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * В данном классе происходит конфигурация {@link SecurityFilterChain}
 *
 * @author Кирилл "Tamada" Симовин
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {
    /**
     * Объект класса {@link UserAuthenticationEntryPoint}, реализующего точку входа аутентификации пользователя
     */
    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;

    /**
     * Объект класса {@link UserAuthProvider} для валидации токена в {@link JwtAuthFilter}
     */
    private final UserAuthProvider userAuthProvider;


    /**
     * В данном бине:
     * <li>Регистрируем точку входа</li>
     * <li>Добавляем в качестве фильтра {@link JwtAuthFilter}</li>
     * <li>Отключаем CSRF</li>
     * <li>Отключаем сохранение информации о пользователе между запросами</li>
     * <li>Авторизовываем запросы по определенным эндпоинтам для всех пользователей: авторизованных и неавторизованных</li>
     *
     * @throws Exception при невалидном токене авторизации пользователя
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(userAuthenticationEntryPoint)
                .and()
                .addFilterBefore(new JwtAuthFilter(userAuthProvider), BasicAuthenticationFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((req) -> req
                        .requestMatchers(HttpMethod.POST,
                                "/auth/login",
                                "/auth",
                                "/auth/register",
                                "/parseLogs/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/ws/**",
                                "/getFullEvent/**",
                                "/getNotifications/*",
                                "/country",
                                "/isAdmin/*",
                                "/getFullPlayer/*",
                                "/getSearchData",
                                "/getMatchStreams/*",
                                "/getFullMatch/**",
                                "/getScoreboard/**",
                                "/getLogs/**",
                                "/getFullMatches/*",
                                "/getPlayerAttendedEvents/*",
                                "/getPlayerResults/*",
                                "/getAllResults",
                                "/getFullTeam/**",
                                "/getTeamAttendedEvents/*",
                                "/getTeamResults/*",
                                "/getPlayersWithoutTeams",
                                "/getFullTop",
                                "/getFullTournaments/*",
                                "/getImage/**",
                                "/getCurrentMap/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
