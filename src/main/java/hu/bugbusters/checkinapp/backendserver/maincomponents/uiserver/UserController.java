package hu.bugbusters.checkinapp.backendserver.maincomponents.uiserver;

import hu.bugbusters.checkinapp.backendserver.dto.UserMeDTO;
import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserMeDTO> getMyDetails(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        UserMeDTO dto = new UserMeDTO(user.getName(), user.getEmail());

        return ResponseEntity.ok(dto);
    }
}