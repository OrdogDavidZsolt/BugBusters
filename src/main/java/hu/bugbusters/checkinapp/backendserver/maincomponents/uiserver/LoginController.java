package hu.bugbusters.checkinapp.backendserver.maincomponents.uiserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import hu.bugbusters.checkinapp.backendserver.dto.LoginRequestDTO;
import hu.bugbusters.checkinapp.backendserver.dto.LoginResponseDTO;
import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.UserRepository;
import java.util.Collections;
import java.util.Optional;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole))
            );

            SecurityContext sc = SecurityContextHolder.createEmptyContext();
            sc.setAuthentication(authentication);
            SecurityContextHolder.setContext(sc);
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

            return ResponseEntity.ok(new LoginResponseDTO(true));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponseDTO(false, "Hibás bejelentkezési mód!"));
        }
    }
}