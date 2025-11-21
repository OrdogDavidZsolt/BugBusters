package hu.bugbusters.checkinapp.backendserver.dto;

import lombok.Data;
import java.util.List;

@Data
public class ClassDetailsDTO {
    private Long sessionId;
    private String name;
    private String location;
    private String dateTime;
    private String teacher;
    private List<StudentAttendanceDTO> students;
}