package Controller;

import Model.User;
import Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {

        //Email alapján megkeressük a felhasználót
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        //Ellenőrzés: csak tanároknál van jelszó
        if (user.getRole() != User.UserRole.TEACHER) {
            return ResponseEntity.status(403).body("Only teachers can log in");
        }

        //Jelszóellenőrzés (egyszerű összehasonlítás — később érdemes bcrypt-re váltani)
        if (!user.getHashedPassword().equals(password)) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        //Ha minden rendben: visszaküldjük a tanár adatait (pl. id + name)
        return ResponseEntity.ok(new LoginResponse(user.getId(), user.getName(), user.getEmail()));
    }

    // Helper DTO osztály
    public record LoginResponse(Long id, String name, String email) {}
}
