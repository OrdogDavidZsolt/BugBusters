package JPAEntityDAO;

import Dao.CourseDAO;
import JPAUtil.JPAUtil;
import Model.Course;
import Model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JPACourseDAO implements CourseDAO {

    private final EntityManager entityManager = JPAUtil.getEntityManager();

    @Override
    public void saveCourse(Course course) {
        entityManager.getTransaction().begin();
        entityManager.persist(course);
        entityManager.getTransaction().commit();
    }

    @Override
    public Course getCourseById(Long id) {
        return entityManager.find(Course.class, id);
    }

    @Override
    public List<Course> getCoursesByTeacher(User teacher) {
        TypedQuery<Course> query = entityManager.createQuery(
                "SELECT c FROM Course c WHERE c.teacher = :teacher", Course.class);
        query.setParameter("teacher", teacher);
        return query.getResultList();
    }

    @Override
    public List<Course> getAllCourses() {
        TypedQuery<Course> query = entityManager.createQuery(
                "SELECT c FROM Course c", Course.class);
        return query.getResultList();
    }

    @Override
    public void updateCourse(Course course) {
        entityManager.getTransaction().begin();
        entityManager.merge(course);
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteCourse(Course course) {
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.contains(course) ? course : entityManager.merge(course));
        entityManager.getTransaction().commit();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
