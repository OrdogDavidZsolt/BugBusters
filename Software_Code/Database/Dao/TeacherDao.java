package Software_Code.Database.Dao;

import Software_Code.Database.Model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherDao extends JpaRepository<Teacher, Long> {

    // Lekérdezés kártyaszám alapján
    Optional<Teacher> findByKartyaszam(String kartyaszam);

    // Lekérdezés név alapján (ha több találat lehet)
    Optional<Teacher> findByNev(String nev);
}