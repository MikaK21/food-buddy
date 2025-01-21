package com.foodbuddy.food_buddy;

import com.foodbuddy.food_buddy.model.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

    @Autowired
    private MyUserDetailService userDetailService; // dependency injection

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/home", "/register/**").permitAll();
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    registry.requestMatchers("/user/**").hasRole("USER");
                    registry.anyRequest().authenticated();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .successHandler(new AuthenticationSuccessHandler())
                            .permitAll();
                })
                .logout(logout -> logout //fix for not redirecting after login
                        .logoutSuccessUrl("/login?logout") // Weiterleiten nach dem Logout
                        .invalidateHttpSession(true) // Session invalidieren
                        .deleteCookies("JSESSIONID", "remember-me") // Cookies löschen
                        .permitAll()
                )
                .build();
    }


//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails normalUser = User.builder()
//            .username("client")
//            .password("$2a$12$c5xp2b3IC1vYx9IwrP3r5.TRBBvTWjriJ5RWiiNWq5QkMmhY776Yu")
//            .roles("USER")
//            .build();
//        UserDetails adminUser = User.builder()
//                .username("admin")
//                .password("$2a$12$Ewq7TgaMPbEcduxhxo8VMuS.ptkk7MSNn5m7H75SkpGaPRdoNZ..G")
//                .roles("ADMIN", "USER")
//                .build();
//        return new InMemoryUserDetailsManager(normalUser, adminUser);
//    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailService;
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
