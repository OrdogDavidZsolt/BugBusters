package hu.bugbusters.checkinapp.database.repository;

import hu.bugbusters.checkinapp.database.model.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {
}
