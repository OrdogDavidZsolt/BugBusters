package rfid.API.DTO;

import lombok.Data;

@Data // Lombok annotation for getters, setters, toString, etc.
public class LoginRequestDTO {
    private String username;
    private String password;
    private String mode; // This will be "admin" or "teacher"
}
