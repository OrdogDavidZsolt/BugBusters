package Dao;

import Model.CourseSession;
import Model.Attendance;
import Model.User;
import java.util.List;

public interface AttendanceDAO {

    void saveAttendance(Attendance attendance);

    Attendance getAttendanceById(Long id);

    List<Attendance> getAttendanceByStudent(User student);

    List<Attendance> getAttendanceBySession(CourseSession session);

    Attendance getAttendanceByStudentAndSession(User student, CourseSession session);

    List<Attendance> getAllAttendance();

    void updateAttendance(Attendance attendance);

    void deleteAttendance(Attendance attendance);
}
