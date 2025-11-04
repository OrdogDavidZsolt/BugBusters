package Software_Code.Database.Manager;

import Software_Code.Database.Dao.AttendanceDAO;
import Software_Code.Database.JPAEntityDAO.JPAAttendanceDAO;
import Software_Code.Database.Model.Attendance;
import Software_Code.Database.Model.CourseSession;
import Software_Code.Database.Model.User;
import java.time.LocalDateTime;

public class AttendanceManager {

    AttendanceDAO attendanceDAO = new  JPAAttendanceDAO();

    public AttendanceManager(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    public void manage() {
        System.out.println("=== Testing Attendance DAO ===");

        // Retrieve existing users and sessions
        User student1 = attendanceDAO.getEntityManager().find(User.class, 1L);
        User student2 = attendanceDAO.getEntityManager().find(User.class, 2L);
        CourseSession session = attendanceDAO.getEntityManager().find(CourseSession.class, 1L);

        // Create attendance records
        Attendance a1 = new Attendance(student1, session, LocalDateTime.now(), true);
        Attendance a2 = new Attendance(student2, session, LocalDateTime.now(), false);

        attendanceDAO.saveAttendance(a1);
        attendanceDAO.saveAttendance(a2);

        System.out.println("\n--- All Attendance Records ---");
        for (Attendance attendance : attendanceDAO.getAllAttendance()) {
            System.out.println(attendance);
        }

        System.out.println("\n--- Attendance for session ---");
        for (Attendance attendance : attendanceDAO.getAttendanceBySession(session)) {
            System.out.println(attendance);
        }

        System.out.println("\n--- Check single student attendance ---");
        Attendance check = attendanceDAO.getAttendanceByStudentAndSession(student1, session);
        System.out.println("Student1 Attendance: " + check);
    }
}
