package com.gutti.store.security;

import com.gutti.store.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)// SOLUCION ES AQUI
@RequiredArgsConstructor
public class SecurityConfig extends VaadinWebSecurity {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final StoreUserDetailsService userDetailsService;

    // Este método ANULA el de la clase base y configura la seguridad para el panel de Vaadin.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // --- SOLUCIÓN DEFINITIVA ---
        // Tomamos el control total del flujo de login y logout para Vaadin.

        // 1. Llamamos a la configuración base de Vaadin para que permita sus recursos internos.
        super.configure(http);

        // 2. Le decimos a Spring que use nuest

        // 2. Le decimos a Spring que use nuestra vista de login personalizada.
        setLoginView(http, LoginView.class);

        // 3. Configuramos el formLogin explícitamente.
        http.formLogin(formLogin ->
                // Le decimos cuál es la página de login.
                formLogin.loginPage("/login").permitAll()
                        // Le decimos que después de un login exitoso, no intente redirigir a una URL guardada,
                        // sino que siempre vaya a la raíz del panel. ESTA ES LA CLAVE.
                        .defaultSuccessUrl("/", true)
        );

        // 4. Configuramos el logout para que acepte peticiones GET y redirija correctamente.
        http.logout(logout ->
                logout.logoutSuccessUrl("/login?logout").permitAll()
        );
    }

    // Este bean, con una prioridad más alta, configura la seguridad para la API REST (stateless)
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // Aplicar esta regla solo a /api/**
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para la API sin estado
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()) // Usa el mismo proveedor de autenticación
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}





