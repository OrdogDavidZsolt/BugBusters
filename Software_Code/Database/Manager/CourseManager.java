package Software_Code.Database.Manager;

import Software_Code.Database.Dao.CourseDAO;
import Software_Code.Database.JPAEntityDAO.JPACourseDAO;
import Software_Code.Database.Model.Course;
import Software_Code.Database.Model.User;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CourseManager {
    CourseDAO courseDAO = new JPACourseDAO();

    public CourseManager(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public void manage() {
        System.out.println("\n=== Testing Course DAO ===");

        EntityManager em = ((JPACourseDAO) courseDAO).getEntityManager();
        // Retrieve all teachers from the DB
        List<User> teachers = em.createQuery(
                        "SELECT u FROM User u WHERE u.role = :role", User.class)
                .setParameter("role", User.UserRole.TEACHER)
                .getResultList();

        if (teachers.size() < 3) {
            System.out.println("Warning: expected at least 3 teachers in DB.");
        }

        User peter = teachers.stream().filter(t -> t.getName().equals("Péter")).findFirst().orElse(null);
        User eszter = teachers.stream().filter(t -> t.getName().equals("Eszter")).findFirst().orElse(null);
        User gabor = teachers.stream().filter(t -> t.getName().equals("Gábor")).findFirst().orElse(null);

        // Example courses
        Course c1 = new Course("Software Engineering", peter);
        Course c2 = new Course("Database Systems", peter);
        Course c3 = new Course("Object-Oriented Programming", peter);

        Course c4 = new Course("Web Development", eszter);
        Course c5 = new Course("Data Structures and Algorithms", eszter);
        Course c6 = new Course("Mobile Application Development", eszter);

        Course c7 = new Course("Computer Networks", gabor);
        Course c8 = new Course("Operating Systems", gabor);
        Course c9 = new Course("Cybersecurity Basics", gabor);
        Course c10 = new Course("Cloud Computing", gabor);

        // Save all
        courseDAO.saveCourse(c1);
        courseDAO.saveCourse(c2);
        courseDAO.saveCourse(c3);
        courseDAO.saveCourse(c4);
        courseDAO.saveCourse(c5);
        courseDAO.saveCourse(c6);
        courseDAO.saveCourse(c7);
        courseDAO.saveCourse(c8);
        courseDAO.saveCourse(c9);
        courseDAO.saveCourse(c10);

        System.out.println("\n--- All Courses ---");
        for (Course c : courseDAO.getAllCourses()) {
            System.out.println(c);
        }

        System.out.println("\n--- Courses taught by each teacher ---");
        for (User t : teachers) {
            System.out.println("\nTeacher: " + t.getName());
            for (Course c : courseDAO.getCoursesByTeacher(t)) {
                System.out.println(" - " + c.getName());
            }
        }
    }
}