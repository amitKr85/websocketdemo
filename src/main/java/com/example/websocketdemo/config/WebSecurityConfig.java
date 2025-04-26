package com.example.websocketdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(obj -> obj.successHandler((req, res, ex) -> {
                            res.setStatus(HttpStatus.OK.value());
                        })
                        .failureHandler((req, res, ex) -> {
                            res.setStatus(HttpStatus.UNAUTHORIZED.value());
                        }))
                .logout(obj -> obj.logoutSuccessHandler((req, res, auth) -> {
                    res.setStatus(HttpStatus.OK.value());
                }))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/index.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/app.js").permitAll()
                        .anyRequest().authenticated())
                .cors(corsCustomizer -> {
                    CorsConfigurationSource configurationSource = request -> {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000"));
                        corsConfiguration.setAllowedMethods(List.of("POST", "GET", "OPTIONS"));
                        corsConfiguration.setAllowCredentials(true);
                        return corsConfiguration;
                    };
                    corsCustomizer.configurationSource(configurationSource);
                })
                .build();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .formLogin()
//                .loginProcessingUrl("/login")
//                .loginPage("/")
//                .defaultSuccessUrl("/chat")
//                .and()
//                .logout()
//                .logoutSuccessUrl("/")
//                .and()
//                .authorizeRequests()
//                .antMatchers("/login", "/new-account", "/").permitAll()
//                .antMatchers(HttpMethod.POST, "/chatroom").hasRole("ADMIN")
//                .anyRequest().authenticated();
//    }
}
