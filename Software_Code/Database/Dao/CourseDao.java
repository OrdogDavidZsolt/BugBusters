package Software_Code.Database.Dao;

import Software_Code.Database.Model.Course;
import Software_Code.Backend_Server.DB_Connection.DB_Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {
    public void addCourse(Course course) {
        String sql = "INSERT INTO course (course_id, teacher_id, student_id) VALUES (?, ?, ?)";

        try (Connection conn = DB_Connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, course.getCourse_id());
            stmt.setInt(2, course.getTeacher_id());
            stmt.setInt(3, course.getStudent_id());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Course> getAllCourses() {
        String sql = "SELECT * FROM course";
        List<Course> courses = new ArrayList<>();

        try (Connection conn = DB_Connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getInt("teacher_id"),
                        rs.getInt("student_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }
}
