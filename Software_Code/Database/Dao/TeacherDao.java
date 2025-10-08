package Software_Code.Database.Dao;

import Software_Code.Database.Model.Teacher;
import Software_Code.Backend_Server.DB_Connection.DB_Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDao {
    public void addTeacher(Teacher teacher) {
        String sql = "INSERT INTO teacher (teacher_id, name, email) VALUES (?, ?, ?)";

        try (Connection conn = DB_Connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacher.getId());
            stmt.setString(2, teacher.getName());
            stmt.setString(3, teacher.getEmail());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Teacher> getAllTeachers() {
        String sql = "SELECT * FROM teacher";
        List<Teacher> teachers = new ArrayList<>();

        try (Connection conn = DB_Connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                teachers.add(new Teacher(
                        rs.getInt("teacher_id"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teachers;
    }
}
