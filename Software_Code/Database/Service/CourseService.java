/*
package Software_Code.Database.Service;

import Software_Code.Database.Dao.CourseDao;
import Software_Code.Database.Model.Course;
import java.util.List;

public class CourseService {
    private final CourseDao courseDao = new CourseDao();

    public void registerCourse(int course_id, int teacher_id, int student_id) {
        Course course = new Course(course_id, teacher_id, student_id);
        courseDao.addCourse(course);
    }

    public List<Course> listAllCourses() {
        return courseDao.getAllCourses();
    }
}
*/