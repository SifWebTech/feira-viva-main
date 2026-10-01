package br.com.feiraviva.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API REST stateless: não usamos CSRF (proteção baseada em cookie de sessão)
                .csrf(csrf -> csrf.disable())

                // Sem sessão HTTP: a identidade virá por token JWT (Aula 15)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // O console do H2 usa iframe; permitimos frames da mesma origem
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                .authorizeHttpRequests(auth -> auth
                        // PÚBLICO: catálogo
                        .requestMatchers("/produtos/**", "/categorias/**").permitAll()

                        // PÚBLICO: documentação e ferramentas de dev
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // PÚBLICO: cadastro de cliente (registro)
                        .requestMatchers(HttpMethod.POST, "/clientes").permitAll()

                        // AULA 15: o restante exigirá JWT.
                        // Por enquanto mantemos aberto para não quebrar o fluxo das Aulas 09–12.
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}