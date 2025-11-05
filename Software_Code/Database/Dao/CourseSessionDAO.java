package Dao;

import JPAUtil.JPAUtil;
import Model.Course;
import Model.CourseSession;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CourseSessionDAO {

    EntityManager em = JPAUtil.getEntityManager();

    void saveSession(CourseSession session);

    CourseSession getSessionById(Long id);

    List<CourseSession> getSessionsByCourse(Course course);

    List<CourseSession> getSessionsByDate(LocalDate date);

    List<CourseSession> getActiveSessions(LocalDateTime now);

    List<CourseSession> getAllSessions();

    void updateSession(CourseSession session);

    void deleteSession(CourseSession session);
}
