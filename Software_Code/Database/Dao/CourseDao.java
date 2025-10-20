package Software_Code.Database.Dao;

import Software_Code.Database.Model.Course;
import Software_Code.Database.Model.Teacher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseDao extends JpaRepository<Course, Long> {

    // Lekérdezés név alapján
    List<Course> findByNevContainingIgnoreCase(String nev);

    // Lekérdezés tanár alapján
    List<Course> findByTanar(Teacher tanar);

    // Lekérdezés tanár ID alapján
    List<Course> findByTanar_Id(Long tanarId);
}
