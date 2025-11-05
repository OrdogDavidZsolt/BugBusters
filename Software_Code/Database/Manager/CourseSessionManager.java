package Software_Code.Database.Manager;

import Software_Code.Database.Dao.CourseSessionDAO;
import Software_Code.Database.JPAEntityDAO.JPACourseSessionDAO;
import Software_Code.Database.Model.Course;
import Software_Code.Database.Model.CourseSession;
import Software_Code.Database.Model.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CourseSessionManager {
    CourseSessionDAO courseSessionDAO = new JPACourseSessionDAO();

    public CourseSessionManager(CourseSessionDAO courseSessionDAO) {
        this.courseSessionDAO = courseSessionDAO;
    }

    public void manage() {
        System.out.println("\n=== Testing CourseSession DAO ===");

        EntityManager em = ((JPACourseSessionDAO) courseSessionDAO).getEntityManager();

        // --- Fetch some existing courses and teachers ---
        List<Course> courses = em.createQuery("SELECT c FROM Course c", Course.class)
                .setMaxResults(5)
                .getResultList();

        if (courses.isEmpty()) {
            System.out.println("No courses found in DB. Add some courses first!");
            return;
        }

        // --- Create multiple sessions for several courses ---
        LocalDate baseDate = LocalDate.now();

        CourseSession s1 = new CourseSession(
                courses.get(0),
                baseDate,
                LocalDateTime.of(baseDate, java.time.LocalTime.of(8, 0)),
                LocalDateTime.of(baseDate, java.time.LocalTime.of(10, 0))
        );

        CourseSession s2 = new CourseSession(
                courses.get(1),
                baseDate.plusDays(1),
                LocalDateTime.of(baseDate.plusDays(1), java.time.LocalTime.of(10, 0)),
                LocalDateTime.of(baseDate.plusDays(1), java.time.LocalTime.of(12, 0))
        );

        CourseSession s3 = new CourseSession(
                courses.get(2),
                baseDate.plusDays(2),
                LocalDateTime.of(baseDate.plusDays(2), java.time.LocalTime.of(13, 0)),
                LocalDateTime.of(baseDate.plusDays(2), java.time.LocalTime.of(15, 0))
        );

        CourseSession s4 = new CourseSession(
                courses.get(0),
                baseDate.plusDays(3),
                LocalDateTime.of(baseDate.plusDays(3), java.time.LocalTime.of(8, 0)),
                LocalDateTime.of(baseDate.plusDays(3), java.time.LocalTime.of(10, 0))
        );

        CourseSession s5 = new CourseSession(
                courses.get(3),
                baseDate.plusDays(4),
                LocalDateTime.of(baseDate.plusDays(4), java.time.LocalTime.of(9, 30)),
                LocalDateTime.of(baseDate.plusDays(4), java.time.LocalTime.of(11, 30))
        );

        CourseSession s6 = new CourseSession(
                courses.get(4),
                baseDate.plusDays(5),
                LocalDateTime.of(baseDate.plusDays(5), java.time.LocalTime.of(14, 0)),
                LocalDateTime.of(baseDate.plusDays(5), java.time.LocalTime.of(16, 0))
        );

        // --- Save all sessions ---
        courseSessionDAO.saveSession(s1);
        courseSessionDAO.saveSession(s2);
        courseSessionDAO.saveSession(s3);
        courseSessionDAO.saveSession(s4);
        courseSessionDAO.saveSession(s5);
        courseSessionDAO.saveSession(s6);

        // --- Display all sessions ---
        System.out.println("\n--- All Sessions ---");
        for (CourseSession s : courseSessionDAO.getAllSessions()) {
            System.out.println(s);
        }

        // --- Example: Get sessions for a specific course ---
        Course sampleCourse = courses.get(0);
        System.out.println("\n--- Sessions for course: " + sampleCourse.getName() + " ---");
        for (CourseSession s : courseSessionDAO.getSessionsByCourse(sampleCourse)) {
            System.out.println(s);
        }
    }
}
