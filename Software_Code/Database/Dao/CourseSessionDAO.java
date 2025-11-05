package Dao;

import Model.Course;
import Model.CourseSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CourseSessionDAO {

    void saveSession(CourseSession session);

    CourseSession getSessionById(Long id);

    List<CourseSession> getSessionsByCourse(Course course);

    List<CourseSession> getSessionsByDate(LocalDate date);

    List<CourseSession> getActiveSessions(LocalDateTime now);

    List<CourseSession> getAllSessions();

    void updateSession(CourseSession session);

    void deleteSession(CourseSession session);
}
