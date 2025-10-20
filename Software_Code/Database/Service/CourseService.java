package Software_Code.Database.Service;

import Software_Code.Database.Dao.CourseDao;
import Software_Code.Database.Model.Course;
import Software_Code.Database.Model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseDao courseDao;

    public Course createCourse(Course course) {
        return courseDao.save(course);
    }

    public List<Course> getAllCourses() {
        return courseDao.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseDao.findById(id);
    }

    public Optional<Course> updateCourse(Long id, Course updatedCourse) {
        return courseDao.findById(id).map(existingCourse -> {
            existingCourse.setNev(updatedCourse.getNev());
            existingCourse.setTanar(updatedCourse.getTanar());
            return courseDao.save(existingCourse);
        });
    }

    public boolean deleteCourse(Long id) {
        if (courseDao.existsById(id)) {
            courseDao.deleteById(id);
            return true;
        }
        return false;
    }

    // Custom finders
    public List<Course> searchByName(String nev) {
        return courseDao.findByNevContainingIgnoreCase(nev);
    }

    public List<Course> findByTeacher(Teacher teacher) {
        return courseDao.findByTanar(teacher);
    }

    public List<Course> findByTeacherId(Long teacherId) {
        return courseDao.findByTanar_Id(teacherId);
    }
}

