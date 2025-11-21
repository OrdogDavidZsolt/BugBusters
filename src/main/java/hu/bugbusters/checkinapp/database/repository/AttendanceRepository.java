package hu.bugbusters.checkinapp.database.repository;

import hu.bugbusters.checkinapp.database.model.Attendance;
import hu.bugbusters.checkinapp.database.model.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySession_Id(Long sessionId);

    //Lekérdezi az összes jelenléti adatot egy adott órához
    List<Attendance> findBySession(CourseSession session);
}