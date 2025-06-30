package com.mysite.xtra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.mysite.xtra.user.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/ws"),
                    new AntPathRequestMatcher("/ws/**"),
                    new AntPathRequestMatcher("/topic/**"),
                    new AntPathRequestMatcher("/queue/**"),
                    new AntPathRequestMatcher("/app/**"),
                    new AntPathRequestMatcher("/user/verify-email")
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    new AntPathRequestMatcher("/admin/**")
                ).hasRole("ADMIN")
                .requestMatchers(
                    new AntPathRequestMatcher("/property/new"),
                    new AntPathRequestMatcher("/property/edit/**"),
                    new AntPathRequestMatcher("/property/delete/**")
                ).authenticated()
                .requestMatchers(
                    new AntPathRequestMatcher("/user/verify-email"),
                    new AntPathRequestMatcher("/user/find-id"),
                    new AntPathRequestMatcher("/user/find-password"),
                    new AntPathRequestMatcher("/user/reset-password")
                ).permitAll()
                .requestMatchers(
                    new AntPathRequestMatcher("/user/login"),
                    new AntPathRequestMatcher("/user/signup"),
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/job/list"),
                    new AntPathRequestMatcher("/job/detail/**"),
                    new AntPathRequestMatcher("/work/list"),
                    new AntPathRequestMatcher("/work/detail/**"),
                    new AntPathRequestMatcher("/offer/detail/**"),
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/style/**"),
                    new AntPathRequestMatcher("/webjars/**"),
                    new AntPathRequestMatcher("/property"),
                    new AntPathRequestMatcher("/property/**")
                ).permitAll()
                .requestMatchers(
                    new AntPathRequestMatcher("/ws"),
                    new AntPathRequestMatcher("/ws/**"),
                    new AntPathRequestMatcher("/topic/**"),
                    new AntPathRequestMatcher("/queue/**"),
                    new AntPathRequestMatcher("/app/**"),
                    new AntPathRequestMatcher("/chat/**")
                ).authenticated()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
            .formLogin(formLogin -> formLogin
                .loginPage("/user/login")
                .defaultSuccessUrl("/")
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
