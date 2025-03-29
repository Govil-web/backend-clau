package com.proyectofinal.clave_compas.security;

import com.proyectofinal.clave_compas.security.jwt.JwtAuthenticationFilter;
import com.proyectofinal.clave_compas.security.userdetail.CusUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CusUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList(
            HttpMethod.OPTIONS.name(),
            HttpMethod.GET.name(),
            HttpMethod.POST.name(), 
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name()
        ));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type",
            "X-Requested-With", 
            "Access-Control-Allow-Origin"
        ));
        
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers.frameOptions(FrameOptionsConfig::deny))
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/users/login","/users/register","/users/auth/refresh", "/users/resend-confirmation").permitAll()
                        .requestMatchers(HttpMethod.GET,"/categories","/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/clavecompas/categories").permitAll() //
                        .requestMatchers(HttpMethod.DELETE, "/clavecompas/categories").permitAll() // BOrrar
                        .requestMatchers(HttpMethod.GET,"/products","/products/search/category/**","/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/reservations/availability").permitAll()
                        .requestMatchers(HttpMethod.GET,"/reservations/product/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/clavecompas/reservations").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/favorites/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/favorites").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/favorites/**").authenticated()
                        .requestMatchers(HttpMethod.GET,"/reviews", "/reviews/**", "/reviews/product/**", "/reviews/stats/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/reviews/user/*/product/*").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/reviews").authenticated()
                        .requestMatchers(HttpMethod.GET,"/search/**").permitAll()
                        .requestMatchers("/products").hasAuthority("ADMIN")
                        .requestMatchers("/users").hasAuthority("ADMIN")
                        .anyRequest().authenticated())

                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

}