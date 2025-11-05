package Dao;

import Model.Course;
import Model.User;
import java.util.List;

public interface CourseDAO {


    void saveCourse(Course course);

    Course getCourseById(Long id);

    List<Course> getCoursesByTeacher(User teacher);

    List<Course> getAllCourses();

    void updateCourse(Course course);

    void deleteCourse(Course course);
}
