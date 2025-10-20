package Software_Code.Database.Service;

import Software_Code.Database.Dao.StudentDao;
import Software_Code.Database.Model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentDao studentDao;

    public Student createStudent(Student student) {
        return studentDao.save(student);
    }

    public List<Student> getAllStudents() {
        return studentDao.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentDao.findById(id);
    }

    public Optional<Student> updateStudent(Long id, Student updatedStudent) {
        return studentDao.findById(id).map(existingStudent -> {
            existingStudent.setNev(updatedStudent.getNev());
            existingStudent.setEmail(updatedStudent.getEmail());
            existingStudent.setSzak(updatedStudent.getSzak());
            existingStudent.setKartyaszam(updatedStudent.getKartyaszam());
            return studentDao.save(existingStudent);
        });
    }

    public boolean deleteStudent(Long id) {
        if (studentDao.existsById(id)) {
            studentDao.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Student> getByKartyaszam(String kartyaszam) {
        return studentDao.findByKartyaszam(kartyaszam);
    }

    public List<Student> getBySzak(String szak) {
        return studentDao.findBySzak(szak);
    }

    public List<Student> searchByName(String nev) {
        return studentDao.findByNevContainingIgnoreCase(nev);
    }
}
