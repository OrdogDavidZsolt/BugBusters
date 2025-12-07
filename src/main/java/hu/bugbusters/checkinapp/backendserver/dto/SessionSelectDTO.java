package hu.bugbusters.checkinapp.backendserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionSelectDTO {
    private Long id;
    private String displayName;
}
