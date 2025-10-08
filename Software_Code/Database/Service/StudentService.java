package Software_Code.Database.Service;

import Software_Code.Database.Dao.StudentDao;
import Software_Code.Database.Model.Student;
import java.util.List;

public class StudentService {
    private final StudentDao studentDao = new StudentDao();

    public void registerStudent(int student_id, String name, String major, String email) {
        Student student = new Student(student_id, name, major, email);
        studentDao.addStudent(student);
    }

    public List<Student> listAllStudents() {
        return studentDao.getAllStudents();
    }
}
