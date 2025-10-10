package Software_Code.Database.Service;

import jakarta.persistence.*;
import Software_Code.Database.Model.*;

public class CheckinService {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("CheckinPU");

    public void checkIn(String cardId, int courseId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Card card = em.find(Card.class, cardId);
        Course course = em.find(Course.class, courseId);

        if (card == null || course == null) {
            System.out.println("❌ Invalid card or lesson");
            em.getTransaction().rollback();
            em.close();
            return;
        }

        Checkin checkin = new Checkin(card, course);
        em.persist(checkin);
        em.getTransaction().commit();
        em.close();

        System.out.println("✅ Check-in saved for student: " + card.getStudent().getName());
    }

    public void close() {
        emf.close();
    }
}

