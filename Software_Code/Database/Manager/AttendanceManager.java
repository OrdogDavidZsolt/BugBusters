package Software_Code.Database.Manager;

import Software_Code.Database.Dao.AttendanceDAO;
import Software_Code.Database.JPAEntityDAO.JPAAttendanceDAO;
import Software_Code.Database.Model.Attendance;
import Software_Code.Database.Model.Attendance.AttendanceStatus;
import Software_Code.Database.Model.CourseSession;
import Software_Code.Database.Model.User;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class AttendanceManager {

    AttendanceDAO attendanceDAO = new JPAAttendanceDAO();

    public AttendanceManager(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    public void manage() {
        System.out.println("\n=== Testing Attendance DAO ===");

        EntityManager em = attendanceDAO.getEntityManager();

        // --- Fetch students and sessions from DB ---
        List<User> students = em.createQuery(
                        "SELECT u FROM User u WHERE u.role = :role", User.class)
                .setParameter("role", User.UserRole.STUDENT)
                .setMaxResults(6)
                .getResultList();

        List<CourseSession> sessions = em.createQuery(
                        "SELECT s FROM CourseSession s", CourseSession.class)
                .setMaxResults(3)
                .getResultList();

        if (students.isEmpty() || sessions.isEmpty()) {
            System.out.println("No students or sessions found in DB. Please populate them first.");
            return;
        }

        Random random = new Random();

        // --- Create attendance records with realistic timing ---
        for (CourseSession session : sessions) {
            System.out.println("\nProcessing session: " + session.getCourse().getName() + " (" + session.getDate() + ")");

            for (User student : students) {
                boolean scannedIn = random.nextDouble() < 0.8; // 80% chance they scanned
                LocalDateTime scannedAt = null;
                AttendanceStatus status;

                if (scannedIn) {
                    // Random scan time between start and start+40 minutes
                    int randomMinuteOffset = random.nextInt(41);
                    scannedAt = session.getStartTime().plusMinutes(randomMinuteOffset);

                    long minutesLate = Duration.between(session.getStartTime(), scannedAt).toMinutes();

                    if (minutesLate <= 20) {
                        status = AttendanceStatus.PRESENT;
                        System.out.println(student.getName() + " -> Present (" + minutesLate + " min after start)");
                    } else {
                        status = AttendanceStatus.LATE;
                        System.out.println(student.getName() + " -> Late (" + minutesLate + " min after start)");
                    }
                } else {
                    // Did not scan in at all → Absent
                    scannedAt = null;
                    status = AttendanceStatus.ABSENT;
                    System.out.println(student.getName() + " -> Absent (no scan)");
                }

                Attendance attendance = new Attendance(
                        student,
                        session,
                        scannedAt,
                        status
                );

                attendanceDAO.saveAttendance(attendance);
            }
        }

        // --- Print all attendance records ---
        System.out.println("\n--- All Attendance Records ---");
        for (Attendance attendance : attendanceDAO.getAllAttendance()) {
            System.out.println(attendance);
        }

        // --- Example: Attendance for a specific session ---
        CourseSession targetSession = sessions.get(0);
        System.out.println("\n--- Attendance for session: " + targetSession.getCourse().getName() + " ---");
        for (Attendance attendance : attendanceDAO.getAttendanceBySession(targetSession)) {
            System.out.println(attendance);
        }
    }
}
