package Software_Code.Database.Service;

import Software_Code.Database.Dao.TeacherDao;
import Software_Code.Database.Model.Teacher;
import java.util.List;

public class TeacherService {
    private final TeacherDao teacherDao = new TeacherDao();

    public void registerTeacher(int teacher_id, String name, String email) {
        Teacher teacher = new Teacher(teacher_id, name, email);
        teacherDao.addTeacher(teacher);
    }

    public List<Teacher> listAllTeachers() {
        return teacherDao.getAllTeachers();
    }
}
