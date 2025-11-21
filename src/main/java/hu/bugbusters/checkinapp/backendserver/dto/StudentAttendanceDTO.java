package hu.bugbusters.checkinapp.backendserver.dto;

import lombok.Data;

@Data
public class StudentAttendanceDTO {
    private Long attendanceId;
    private String name;
    private String code;
    private String time;
    private String note;
}