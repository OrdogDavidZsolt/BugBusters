package Software_Code.Database.Manager;

import Software_Code.Database.Dao.CourseSessionDAO;
import Software_Code.Database.JPAEntityDAO.JPACourseSessionDAO;
import Software_Code.Database.Model.Course;
import Software_Code.Database.Model.CourseSession;
import Software_Code.Database.Model.User;
import jakarta.persistence.EntityManager;

public class CourseSessionManager {
    CourseSessionDAO courseSessionDAO = new JPACourseSessionDAO();

    public CourseSessionManager(CourseSessionDAO courseSessionDAO) {
        this.courseSessionDAO = courseSessionDAO;
    }

    public void manage() {
        System.out.println("=== Testing CourseSession DAO ===");

        // Get an EntityManager from the DAO impl
        EntityManager em = ((JPACourseSessionDAO) courseSessionDAO).getEntityManager();

        // --- Fetch existing teacher and course from DB ---
        User teacher = em.createQuery(
                        "SELECT u FROM User u WHERE u.role = :role", User.class)
                .setParameter("role", User.UserRole.TEACHER)
                .setMaxResults(1)
                .getSingleResult();

        Course course = em.createQuery(
                        "SELECT c FROM Course c WHERE c.teacher = :teacher", Course.class)
                .setParameter("teacher", teacher)
                .setMaxResults(1)
                .getSingleResult();

        // --- Create new session ---
        CourseSession session = new CourseSession(
                course,
                java.time.LocalDate.now(),
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now().plusHours(2)
        );

        courseSessionDAO.saveSession(session);

        System.out.println("\n--- All Sessions ---");
        for (CourseSession s : courseSessionDAO.getAllSessions()) {
            System.out.println(s);
        }
    }

}
