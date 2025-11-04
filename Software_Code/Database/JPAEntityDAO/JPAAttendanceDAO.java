package Software_Code.Database.JPAEntityDAO;

import Software_Code.Database.Dao.AttendanceDAO;
import Software_Code.Database.JPAUtil;
import Software_Code.Database.Model.Attendance;
import Software_Code.Database.Model.CourseSession;
import Software_Code.Database.Model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JPAAttendanceDAO implements AttendanceDAO {

    private final EntityManager entityManager = JPAUtil.getEntityManager();

    @Override
    public void saveAttendance(Attendance attendance) {
        em.getTransaction().begin();
        attendance.setStudent(em.merge(attendance.getStudent()));
        attendance.setSession(em.merge(attendance.getSession()));
        em.persist(attendance);
        em.getTransaction().commit();
    }

    @Override
    public Attendance getAttendanceById(Long id) {
        return entityManager.find(Attendance.class, id);
    }

    @Override
    public List<Attendance> getAttendanceByStudent(User student) {
        TypedQuery<Attendance> query = entityManager.createQuery(
                "SELECT a FROM Attendance a WHERE a.student = :student", Attendance.class);
        query.setParameter("student", student);
        return query.getResultList();
    }

    @Override
    public List<Attendance> getAttendanceBySession(CourseSession session) {
        TypedQuery<Attendance> query = entityManager.createQuery(
                "SELECT a FROM Attendance a WHERE a.session = :session", Attendance.class);
        query.setParameter("session", session);
        return query.getResultList();
    }

    @Override
    public Attendance getAttendanceByStudentAndSession(User student, CourseSession session) {
        TypedQuery<Attendance> query = entityManager.createQuery(
                "SELECT a FROM Attendance a WHERE a.student = :student AND a.session = :session",
                Attendance.class);
        query.setParameter("student", student);
        query.setParameter("session", session);

        List<Attendance> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Attendance> getAllAttendance() {
        TypedQuery<Attendance> query = entityManager.createQuery(
                "SELECT a FROM Attendance a", Attendance.class);
        return query.getResultList();
    }

    @Override
    public void updateAttendance(Attendance attendance) {
        entityManager.getTransaction().begin();
        entityManager.persist(attendance);
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteAttendance(Attendance attendance) {
        entityManager.getTransaction().begin();
        entityManager.remove(attendance);
        entityManager.getTransaction().commit();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
