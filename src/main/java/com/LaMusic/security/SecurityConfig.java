package com.LaMusic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/auth/**").permitAll()

                // Endpoints de leitura pública (ex: categorias)
//                .requestMatchers("/categories").hasAnyRole("ADMIN", "USER")
//                .requestMatchers("/categories/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/categories/admin", "/categories/admin/**").hasRole("ADMIN")


                // Endpoints ADMIN
//                .requestMatchers("categories/admin/**").hasRole("ADMIN")

                // Endpoints de cliente logado
//                .requestMatchers("/client/**").hasRole("USER")

                // Qualquer outra requisição precisa de autenticação
                .anyRequest().permitAll()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
