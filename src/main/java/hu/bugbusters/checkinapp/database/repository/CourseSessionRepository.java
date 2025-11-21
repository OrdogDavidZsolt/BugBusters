package hu.bugbusters.checkinapp.database.repository;

import hu.bugbusters.checkinapp.database.model.Course;
import hu.bugbusters.checkinapp.database.model.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {
    List<CourseSession> findByCourse_Id(Long courseId);

    //Lekérdezi egy kurzus legutóbbi (vagy aktív) óráját
    Optional<CourseSession> findTopByCourseOrderByIdDesc(Course course);
}