package edu.sv.ues.dam235.apirestdemo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    /**
     * Bean para encriptar contraseñas.
     * DelegatingPasswordEncoder maneja automáticamente el prefijo {bcrypt}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Bean del AuthenticationManager.
     * Spring Security 6/7 lo obtiene desde AuthenticationConfiguration.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Cadena de filtros de seguridad (Spring Security 6/7 style).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Deshabilitar CSRF (API REST stateless)
                .csrf(csrf -> csrf.disable())
                // Deshabilitar CORS (ya lo maneja JwtFilter)
                .cors(cors -> cors.disable())
                // Sin sesiones: JWT es stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configurar rutas públicas y protegidas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (sin token)
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        // Todas las demás requieren autenticación
                        .anyRequest().authenticated()
                );

        // Agregar el filtro JWT antes del filtro de autenticación estándar
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}