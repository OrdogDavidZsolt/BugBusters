package hu.bugbusters.checkinapp.backendserver.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
    private String mode;
}