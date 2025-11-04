package Software_Code.Database.Manager;

import Software_Code.Database.Dao.CourseDAO;
import Software_Code.Database.JPAEntityDAO.JPACourseDAO;
import Software_Code.Database.Model.Course;
import Software_Code.Database.Model.User;
import jakarta.persistence.EntityManager;

public class CourseManager {
    CourseDAO courseDAO = new JPACourseDAO();

    public CourseManager(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public void manage() {
        System.out.println("=== Testing Course DAO ===");

        EntityManager em = ((JPACourseDAO) courseDAO).getEntityManager();
        User teacher = em.createQuery(
                        "SELECT u FROM User u WHERE u.role = :role", User.class)
                .setParameter("role", User.UserRole.TEACHER)
                .setMaxResults(1)
                .getSingleResult();


        Course c1 = new Course("Software Engineering", teacher);
        Course c2 = new Course("Database Systems", teacher);

        courseDAO.saveCourse(c1);
        courseDAO.saveCourse(c2);

        System.out.println("\n--- All Courses ---");
        for (Course c : courseDAO.getAllCourses()) {
            System.out.println(c);
        }

        System.out.println("\n--- Courses taught by teacher ---");
        for (Course c : courseDAO.getCoursesByTeacher(teacher)) {
            System.out.println(c);
        }
    }

}
