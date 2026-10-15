package devSuivi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    //public SecurityFilterChain securityFilterChain(HttpSecurity http)
    //public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) {
        http
            .cors(Customizer.withDefaults())
            //.formLogin(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/user/me").authenticated()
                    .requestMatchers("/user/{id:[\\d]+}/projects", "/user/{id:[\\d]+}/contributions").authenticated()
                    .requestMatchers(HttpMethod.GET, "/project/{id:[\\d]+}", "/project/{id:[\\d]+}/users", "/project/{id:[\\d]+}/contributions").authenticated()
                    .requestMatchers(HttpMethod.GET, "/contribution").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers("/contribution/**").authenticated()
                    .requestMatchers(HttpMethod.GET,"/user", "/user/{id:[\\d]+}").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers("/user/**").hasRole("ADMIN")
                    .requestMatchers("/server/**", "/project/**", "/assign/**").hasAnyRole("ADMIN", "MANAGER")
                    .anyRequest().authenticated()
            )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(oauth2 ->  oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)))
        ;
        return http.build();
    }

    // Autorise le front Nuxt (npm run dev -> http://localhost:3000) à appeler l'API
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
