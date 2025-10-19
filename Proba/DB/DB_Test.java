package DB;

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

        Teacher t1 = new Teacher("John Doe", "john@example.com");
        Teacher t2 = new Teacher("Jane Smith", "jane@example.com");
        Teacher t3 = new Teacher("Robert Brown", "robert@example.com");
        em.persist(t1);
        em.persist(t2);
        em.persist(t3);

        // --- Students ---
        Student s1 = new Student("Alice", "CS", "alice@example.com");
        Student s2 = new Student("Bob", "Math", "bob@example.com");
        Student s3 = new Student("Clara", "Physics", "clara@example.com");
        em.persist(s1);
        em.persist(s2);
        em.persist(s3);

        // --- Courses ---
        Course c1 = new Course(t1.getId(), s1.getId());
        Course c2 = new Course(t1.getId(), s2.getId());
        Course c3 = new Course(t2.getId(), s3.getId());
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);

        // --- Cards ---
        Card card1 = new Card("CARD123", s1);
        Card card2 = new Card("CARD456", s2);
        Card card3 = new Card("CARD789", s3);
        em.persist(card1);
        em.persist(card2);
        em.persist(card3);

        // --- Checkins ---
        Checkin ch1 = new Checkin(card1, c1);
        Checkin ch2 = new Checkin(card2, c1);
        Checkin ch3 = new Checkin(card3, c2);
        Checkin ch4 = new Checkin(card1, c2);
        em.persist(ch1);
        em.persist(ch2);
        em.persist(ch3);
        em.persist(ch4);

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("http://localhost:8082");
        System.out.println("All entities persisted successfully!");
    }

    private static void startDatabase() throws SQLException {
        Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists").start();
        Server.createWebServer("-webAllowOthers", "-ifNotExists").start();
    }

}


