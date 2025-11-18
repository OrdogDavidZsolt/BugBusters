package rfid.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Preflight kérések
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. A Login oldal (HTML, CSS, JS, Képek) legyen NYILVÁNOS!
                        // Ez hiányzott: a gyökér útvonal és a fájlok
                        .requestMatchers("/", "/index.html", "/style.css", "/js/*.js", "/js/admin/*.js", "/js/teacher/*.js", "/*.ico").permitAll()

                        // 3. A Login API végpont
                        .requestMatchers("/login").permitAll()

                        // 4. H2 Konzol
                        .requestMatchers("/h2-console/**").permitAll()

                        // 5. Minden más (pl. Teachers_Page, Admin_Page) védett marad!
                        .anyRequest().authenticated()
                );

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. PONTOS CÍM MEGADÁSA (a "*" helyett)
        // Engedélyezzük a 80-as portról (a UI szerveredtől) érkező kéréseket.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:80", "http://localhost"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // 2. EZ A LÉNYEG: Engedélyezzük a cookie-k küldését
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}