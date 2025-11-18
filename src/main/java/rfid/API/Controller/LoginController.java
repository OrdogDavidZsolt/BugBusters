package rfid.API.Controller;

import jakarta.servlet.http.HttpServletRequest; // ÚJ IMPORT
import jakarta.servlet.http.HttpSession;        // ÚJ IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // ÚJ IMPORT
import org.springframework.security.core.authority.SimpleGrantedAuthority; // ÚJ IMPORT
import org.springframework.security.core.context.SecurityContext; // ÚJ IMPORT
import org.springframework.security.core.context.SecurityContextHolder; // ÚJ IMPORT
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository; // ÚJ IMPORT
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rfid.API.DTO.LoginRequestDTO;
import rfid.API.DTO.LoginResponseDTO;
import rfid.DB.Model.User;
import rfid.DB.Repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // FONTOS: Hozzáadtuk a HttpServletRequest-et a paraméterekhez!
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {

        String email = loginRequestDTO.getUsername();
        String password = loginRequestDTO.getPassword();
        String requestedMode = loginRequestDTO.getMode();

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDTO(false, "Hibás felhasználónév vagy jelszó!"));
        }

        User user = userOpt.get();

        if (user.getHashedPassword() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDTO(false, "Ehhez a fiókhoz nem tartozik jelszó."));
        }

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDTO(false, "Hibás felhasználónév vagy jelszó!"));
        }

        String userRole = user.getRole().name();

        if (userRole.equalsIgnoreCase(requestedMode)) {

            // --- ITT KEZDŐDIK A JAVÍTÁS: SESSION LÉTREHOZÁSA ---

            // 1. Létrehozunk egy "Token"-t, ami tartalmazza a felhasználót és a jogosultságát (ROLE)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole))
            );

            // 2. Beállítjuk a SecurityContext-et (ez mondja meg a Springnek, hogy "BE VAGY LÉPVE")
            SecurityContext sc = SecurityContextHolder.createEmptyContext();
            sc.setAuthentication(authentication);
            SecurityContextHolder.setContext(sc);

            // 3. Létrehozzuk a HTTP Session-t és elmentjük bele a Context-et
            // Ez generálja le a JSESSIONID cookie-t, amit a böngésző megkap
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

            // --- JAVÍTÁS VÉGE ---

            return ResponseEntity.ok(new LoginResponseDTO(true));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponseDTO(false, "Hibás bejelentkezési mód!"));
        }
    }
}