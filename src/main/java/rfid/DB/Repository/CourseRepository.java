package rfid.DB.Repository;

import rfid.DB.Model.Course;
import rfid.DB.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacher_Id(Long teacherId);

    //Lekérdezi az összes kurzust, amit egy adott tanár tart
    List<Course> findByTeacher(User teacher);
}
