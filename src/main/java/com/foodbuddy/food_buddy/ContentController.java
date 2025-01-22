package com.foodbuddy.food_buddy;

import com.foodbuddy.food_buddy.model.MyUserDetailService;
import com.foodbuddy.food_buddy.webtoken.JwtService;
import com.foodbuddy.food_buddy.webtoken.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {

    @Controller
    public class WebController {
        // Methoden für Seiten, die Thymeleaf-Templates rendern
        @GetMapping("/home")
        public String handleWelcome() {
            return "home";
        }

        @GetMapping("/admin/home")
        public String handleAdminHome() {
            return "home_admin";
        }

        @GetMapping("/user/home")
        public String handleUserHome() {
            return "home_user";
        }

        @GetMapping("/login")
        public String handleLogin() {
            return "custom_login";
        }

        @GetMapping("/registration")
        public String handleRegistration() {
            return "custom_registration";
        }
    }

    @RestController
    public class AuthenticationController {
        @Autowired
        private AuthenticationManager authenticationManager;
        @Autowired
        private JwtService jwtService;
        @Autowired
        private MyUserDetailService myUserDetailService;

        // Methode für die Authentifizierung und Token-Generierung
        @PostMapping("/authenticate")
        public String authenticateAndGetToken(@RequestBody LoginForm loginForm) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginForm.username(), loginForm.password()
            ));
            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(myUserDetailService.loadUserByUsername(loginForm.username()));
            } else {
                throw new UsernameNotFoundException("Invalid credentials");
            }
        }
    }
}
