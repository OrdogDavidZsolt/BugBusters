package Dao;

import JPAUtil.JPAUtil;
import Model.CourseSession;
import Model.Attendance;
import Model.User;
import jakarta.persistence.EntityManager;
import java.util.List;

public interface AttendanceDAO {
    EntityManager em = JPAUtil.getEntityManager();

    EntityManager getEntityManager();

    void saveAttendance(Attendance attendance);

    Attendance getAttendanceById(Long id);

    List<Attendance> getAttendanceByStudent(User student);

    List<Attendance> getAttendanceBySession(CourseSession session);

    Attendance getAttendanceByStudentAndSession(User student, CourseSession session);

    List<Attendance> getAllAttendance();

    void updateAttendance(Attendance attendance);

    void deleteAttendance(Attendance attendance);
}
