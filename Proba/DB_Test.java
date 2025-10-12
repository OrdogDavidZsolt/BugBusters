import Software_Code.Database.Model.*;
import jakarta.persistence.*;
import java.sql.SQLException;
import org.h2.tools.Server;

public class DB_Test {
    public static void main(String[] args) throws SQLException {
        startDatabase();

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

    private static void startDatabase() throws SQLException {
        Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists").start();
        Server.createWebServer("-webAllowOthers", "-ifNotExists").start();
    }

}


