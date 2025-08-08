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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.mysite.xtra.user.UserService;
import com.mysite.xtra.user.CustomAuthenticationSuccessHandler;
import com.mysite.xtra.user.CustomAuthenticationFailureHandler;
import com.mysite.xtra.config.JwtAuthenticationFilter;
import com.mysite.xtra.config.JwtUtil;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;
    // @Autowired
    // private UserDetailsService userDetailsService; // 이 부분 삭제

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, @Lazy UserDetailsService userDetailsService) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")
                .successHandler(new CustomAuthenticationSuccessHandler(jwtUtil))
                .failureHandler(new CustomAuthenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                              org.springframework.security.core.Authentication authentication) 
                                              throws IOException, ServletException {
                        // JWT 쿠키 삭제
                        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("jwt", "");
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        cookie.setSecure(false);
                        response.addCookie(cookie);
                        
                        response.sendRedirect("/");
                    }
                })
                .invalidateHttpSession(true)
                .deleteCookies("jwt")
                .permitAll()
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/user/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/signup")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/"),
                                 new AntPathRequestMatcher("/home"),
                                 new AntPathRequestMatcher("/main"),
                                 new AntPathRequestMatcher("/css/**"),
                                 new AntPathRequestMatcher("/js/**"),
                                 new AntPathRequestMatcher("/images/**"),
                                 new AntPathRequestMatcher("/style/**"),
                                 new AntPathRequestMatcher("/webjars/**")
                ).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/work/list")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/work/detail/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/work/create")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/work/edit/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/work/delete/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/work/modify/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/resume/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/job/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/property")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/property/{id}")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/property/new")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/property/edit/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/property/delete/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/offer/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8083");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
