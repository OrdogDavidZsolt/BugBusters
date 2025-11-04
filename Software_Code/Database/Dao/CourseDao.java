package Software_Code.Database.Dao;

import Software_Code.Database.JPAUtil;
import Software_Code.Database.Model.Course;
import Software_Code.Database.Model.User;
import jakarta.persistence.EntityManager;
import java.util.List;

public interface CourseDAO {

    EntityManager em = JPAUtil.getEntityManager();

    void saveCourse(Course course);

    Course getCourseById(Long id);

    List<Course> getCoursesByTeacher(User teacher);

    List<Course> getAllCourses();

    void updateCourse(Course course);

    void deleteCourse(Course course);
}
