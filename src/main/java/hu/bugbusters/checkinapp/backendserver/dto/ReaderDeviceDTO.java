package hu.bugbusters.checkinapp.backendserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReaderDeviceDTO {
    private String id;
    private String name;
    private String position;
    private String type;
    private String ip;
    private boolean isOnline;
}