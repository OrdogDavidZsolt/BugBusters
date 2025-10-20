package Software_Code.Database.Service;

import Software_Code.Database.Dao.TeacherDao;
import Software_Code.Database.Model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    @Autowired
    private TeacherDao teacherDao;

    public Teacher createTeacher(Teacher teacher) {
        return teacherDao.save(teacher);
    }

    public List<Teacher> getAllTeachers() {
        return teacherDao.findAll();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherDao.findById(id);
    }

    public Optional<Teacher> updateTeacher(Long id, Teacher updatedTeacher) {
        return teacherDao.findById(id).map(existingTeacher -> {
            existingTeacher.setNev(updatedTeacher.getNev());
            existingTeacher.setEmail(updatedTeacher.getEmail());
            existingTeacher.setKartyaszam(updatedTeacher.getKartyaszam());
            return teacherDao.save(existingTeacher);
        });
    }

    public boolean deleteTeacher(Long id) {
        if (teacherDao.existsById(id)) {
            teacherDao.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Teacher> getByKartyaszam(String kartyaszam) {
        return teacherDao.findByKartyaszam(kartyaszam);
    }

    public Optional<Teacher> getByNev(String nev) {
        return teacherDao.findByNev(nev);
    }
}

