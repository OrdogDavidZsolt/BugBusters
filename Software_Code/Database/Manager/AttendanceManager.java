package Manager;

import Dao.AttendanceDAO;
import Model.Attendance;
import Model.Attendance.AttendanceStatus;
import Model.CourseSession;
import Model.User;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class AttendanceManager {

    private final AttendanceDAO attendanceDAO;

    public AttendanceManager(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    public void manage() {
        System.out.println("\n=== Testing Attendance DAO ===");

        // DAO metódusok meghívása
        List<User> students = /* TODO: ezt majd egy UserDAO-ból kérd le */
                List.of(); // ideiglenesen üres lista

        List<CourseSession> sessions = /* TODO: szintén DAO-ból kérd le */
                List.of();

        if (students.isEmpty() || sessions.isEmpty()) {
            System.out.println("No students or sessions found in DB. Please populate them first.");
            return;
        }

        Random random = new Random();

        for (CourseSession session : sessions) {
            for (User student : students) {
                boolean scannedIn = random.nextDouble() < 0.8;
                LocalDateTime scannedAt = null;
                AttendanceStatus status;

                if (scannedIn) {
                    int randomMinuteOffset = random.nextInt(41);
                    scannedAt = session.getStartTime().plusMinutes(randomMinuteOffset);
                    long minutesLate = Duration.between(session.getStartTime(), scannedAt).toMinutes();
                    status = (minutesLate <= 20) ? AttendanceStatus.PRESENT : AttendanceStatus.LATE;
                } else {
                    status = AttendanceStatus.ABSENT;
                }

                Attendance attendance = new Attendance(student, session, scannedAt, status);
                attendanceDAO.saveAttendance(attendance);
            }
        }

        System.out.println("\n--- All Attendance Records ---");
        for (Attendance attendance : attendanceDAO.getAllAttendance()) {
            System.out.println(attendance);
        }
    }
}
