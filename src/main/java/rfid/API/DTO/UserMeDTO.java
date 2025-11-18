package rfid.API.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserMeDTO {
    private String name;
    private String email;
}
