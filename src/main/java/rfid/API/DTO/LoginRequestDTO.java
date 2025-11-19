package rfid.API.DTO;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
    private String mode;
}
