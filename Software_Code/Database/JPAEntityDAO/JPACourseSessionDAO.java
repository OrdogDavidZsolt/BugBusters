package JPAEntityDAO;

import Dao.CourseSessionDAO;
import Model.Course;
import Model.CourseSession;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JPACourseSessionDAO implements CourseSessionDAO {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveSession(CourseSession session) {
        entityManager.getTransaction().begin();
        entityManager.persist(session);
        entityManager.getTransaction().commit();
    }

    @Override
    public CourseSession getSessionById(Long id) {
        return entityManager.find(CourseSession.class, id);
    }

    @Override
    public List<CourseSession> getSessionsByCourse(Course course) {
        TypedQuery<CourseSession> query = entityManager.createQuery(
                "SELECT cs FROM CourseSession cs WHERE cs.course = :course", CourseSession.class);
        query.setParameter("course", course);
        return query.getResultList();
    }

    @Override
    public List<CourseSession> getSessionsByDate(LocalDate date) {
        TypedQuery<CourseSession> query = entityManager.createQuery(
                "SELECT cs FROM CourseSession cs WHERE cs.date = :date", CourseSession.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<CourseSession> getActiveSessions(LocalDateTime now) {
        TypedQuery<CourseSession> query = entityManager.createQuery(
                "SELECT cs FROM CourseSession cs WHERE cs.startTime <= :now AND cs.endTime >= :now",
                CourseSession.class
        );
        query.setParameter("now", now);
        return query.getResultList();
    }

    @Override
    public List<CourseSession> getAllSessions() {
        TypedQuery<CourseSession> query = entityManager.createQuery(
                "SELECT cs FROM CourseSession cs", CourseSession.class);
        return query.getResultList();
    }

    @Override
    public void updateSession(CourseSession session) {
        entityManager.getTransaction().begin();
        entityManager.merge(session);
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteSession(CourseSession session) {
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.contains(session) ? session : entityManager.merge(session));
        entityManager.getTransaction().commit();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
