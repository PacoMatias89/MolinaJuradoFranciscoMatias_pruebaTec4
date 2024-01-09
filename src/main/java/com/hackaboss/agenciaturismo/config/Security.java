package com.hackaboss.agenciaturismo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable()
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/agency/hotels/search").permitAll()
                                .requestMatchers("/agency/hotel-booking/new").permitAll()
                                .requestMatchers("/agency/flight-booking/new").permitAll()
                                .requestMatchers("/agency/hotels/**").permitAll()
                                .requestMatchers("/agency/flights/**").permitAll()
                                .requestMatchers("/agency/flights/search").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .permitAll()
                )
                .httpBasic().and()
                .build();
    }
}
