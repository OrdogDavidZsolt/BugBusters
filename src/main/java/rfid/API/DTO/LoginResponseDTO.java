package rfid.API.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private boolean success;
    private String message;

    // Helper constructor for a success-only response
    public LoginResponseDTO(boolean success) {
        this.success = success;
        this.message = null;
    }
}
