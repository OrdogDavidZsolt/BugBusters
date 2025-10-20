package Software_Code.Database.Dao;

import Software_Code.Database.Model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDao extends JpaRepository<Student, Long> {

    // Lekérdezés kártyaszám alapján
    Optional<Student> findByKartyaszam(String kartyaszam);

    // Lekérdezés szak alapján (több hallgató lehet egy szakon)
    List<Student> findBySzak(String szak);

    // Lekérdezés név alapján (nem feltétlen egyedi)
    List<Student> findByNevContainingIgnoreCase(String nev);
}
