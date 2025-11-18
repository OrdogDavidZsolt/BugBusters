package rfid.API.DTO;

import lombok.Data;

@Data
public class StudentAttendanceDTO {
    private Long attendanceId; // Fontos a későbbi szerkesztéshez/törléshez
    private String name;
    private String code; // Neptun kód
    private String time; // Érkezés ideje
    private String note;
}
