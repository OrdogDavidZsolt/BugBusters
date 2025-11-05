package JPAEntityDAO;

import Dao.AttendanceDAO;
import Model.Attendance;
import Model.CourseSession;
import Model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class JPAAttendanceDAO implements AttendanceDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveAttendance(Attendance attendance) {
        attendance.setStudent(entityManager.merge(attendance.getStudent()));
        attendance.setSession(entityManager.merge(attendance.getSession()));
        entityManager.persist(attendance);
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
    @Transactional
    public void updateAttendance(Attendance attendance) {
        entityManager.merge(attendance);
    }

    @Override
    @Transactional
    public void deleteAttendance(Attendance attendance) {
        entityManager.remove(entityManager.contains(attendance) ? attendance : entityManager.merge(attendance));
    }
}
