package hu.bugbusters.checkinapp.backendserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReaderDeviceDTO {
    private String id;   // Csak a szám, pl. "001"
    private String name;
    private String ip;
}
