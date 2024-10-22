package com.projeto.gerenciamento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.projeto.gerenciamento.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/administrador/**")
                .hasRole("ADMIN")
                .requestMatchers("/unidade/**")
                .hasRole("UNIDADE")
                .anyRequest().authenticated()) 
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler()) 
                .permitAll()) 
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                .invalidateHttpSession(true)  
                .deleteCookies("JSESSIONID")  
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))) 
            .exceptionHandling(handling -> handling
                .accessDeniedPage("/access-denied")) 
            .userDetailsService(customUserDetailsService); 

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .get()
                    .getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/administrador");  
            } else if (role.equals("ROLE_UNIDADE")) {
                response.sendRedirect("/unidade");  
            } else {
                response.sendRedirect("/"); 
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
