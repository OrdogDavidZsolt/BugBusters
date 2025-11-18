package rfid.DB.Repository;

import rfid.DB.Model.Attendance;
import rfid.DB.Model.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySession_Id(Long sessionId);

    // ÚJ: Lekérdezi az összes jelenléti adatot egy adott órához
    List<Attendance> findBySession(CourseSession session);
}
