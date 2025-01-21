package com.foodbuddy.food_buddy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/home").permitAll();
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    registry.requestMatchers("/user/**").hasRole("USER");
                    registry.anyRequest().authenticated();
                })
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails normalUser = User.builder()
            .username("client")
            .password("$2a$12$c5xp2b3IC1vYx9IwrP3r5.TRBBvTWjriJ5RWiiNWq5QkMmhY776Yu")
            .roles("USER")
            .build();
        UserDetails adminUser = User.builder()
                .username("admin")
                .password("$2a$12$Ewq7TgaMPbEcduxhxo8VMuS.ptkk7MSNn5m7H75SkpGaPRdoNZ..G")
                .roles("ADMIN", "USER")
                .build();
        return new InMemoryUserDetailsManager(normalUser, adminUser);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
