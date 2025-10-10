import Software_Code.Database.Model.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import Software_Code.Database.Service.CheckinService;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CheckinPU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Persist Teacher
        Teacher teacher = new Teacher("John Doe", "john@example.com");
        em.persist(teacher);

        // Persist Student
        Student student = new Student("Alice", "alice@example.com", "CS");
        em.persist(student);

        // Persist Course
        Course course = new Course(teacher.getId(), student.getId());
        em.persist(course);

        // Persist Card
        Card card = new Card("CARD123", student);
        em.persist(card);

        // Persist Checkin
        Checkin checkin = new Checkin(card, course);
        em.persist(checkin);

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("All entities persisted successfully!");
    }
}
