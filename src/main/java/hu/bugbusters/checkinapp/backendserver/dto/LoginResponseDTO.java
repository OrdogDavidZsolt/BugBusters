package hu.bugbusters.checkinapp.backendserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private boolean success;
    private String message;

    public LoginResponseDTO(boolean success) {
        this.success = success;
        this.message = null;
    }
}