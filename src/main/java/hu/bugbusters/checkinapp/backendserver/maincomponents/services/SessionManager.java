package hu.bugbusters.checkinapp.backendserver.maincomponents.services;

import hu.bugbusters.checkinapp.backendserver.timer.Timer;
import hu.bugbusters.checkinapp.database.model.Attendance;
import hu.bugbusters.checkinapp.database.model.CourseSession;
import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.AttendanceRepository;
import hu.bugbusters.checkinapp.database.repository.CourseSessionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE   = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN   = "\u001B[36m";
    private static final String WHITE  = "\u001B[37m";
    private static final String PREFIX = CYAN + ">> HW_Connection -> SessionManager: " + RESET;

    private final int SESSION_MINUTES = 1;
    private final int SESSION_SECONDS = 0;

    private final AttendanceRepository attendanceRepository;
    private final CourseSessionRepository courseSessionRepository; // új
    private final Map<Integer, ActiveSession> activeSessions = new ConcurrentHashMap<>();

    @Autowired
    public SessionManager(AttendanceRepository attendanceRepository,
                          CourseSessionRepository courseSessionRepository) {
        this.attendanceRepository = attendanceRepository;
        this.courseSessionRepository = courseSessionRepository;
    }

    public boolean isTeacherSessionActive(int readerID) {
        return activeSessions.containsKey(readerID);
    }

    // Új metódus, ami User-t vár
    public void startTeacherSession(int readerId, User teacher) {
        // lekérdezzük az aktuális kurzust a tanárhoz
        Optional<CourseSession> optionalSession = courseSessionRepository.findTopByCourse_TeacherOrderByIdDesc(teacher);
        if (optionalSession.isPresent()) {
            CourseSession session = optionalSession.get();
            startTeacherSession(readerId, session); // eredeti metódus meghívása
        } else {
            System.out.println("Nincs aktív kurzus a tanárhoz: " + teacher.getName());
        }
    }

    // Eredeti metódus változatlan
    public void startTeacherSession(int readerId, CourseSession courseSession) {
        ActiveSession existing = activeSessions.get(readerId);
        if (existing != null) existing.close(); // lezárjuk az előzőt

        ActiveSession session = new ActiveSession(courseSession, readerId);
        activeSessions.put(readerId, session);

        // 20 perces timer a TimingService-szel
        TimingService.startNewTimer(SESSION_MINUTES, SESSION_SECONDS);
        Timer timer = TimingService.getTimers().get(TimingService.getTimers().size() - 1);
        timer.setTimerListener(t -> {
            session.close();
            activeSessions.remove(readerId);
            System.out.println(PREFIX + "Session lezárva a timer által: ReaderID=" + readerId);
        });

        session.setTimer(timer);
    }

    public boolean registerStudentCard(int readerId, User student) {
        ActiveSession session = activeSessions.get(readerId);
        if (session == null || session.isClosed()) return false;

        Attendance attendance = Attendance.builder()
                .student(student)
                .session(session.getCourseSession())
                .status(Attendance.AttendanceStatus.PRESENT)
                .scannedAt(LocalDateTime.now())
                .build();

        attendanceRepository.save(attendance);
        return true;
    }

    public void endTeacherSessionEarly(int readerId) {
        ActiveSession session = activeSessions.get(readerId);
        if (session != null) {
            session.close();
            activeSessions.remove(readerId);
            System.out.println(PREFIX + "Session lezárva tanári kártya által. ReaderID=" + readerId);
        }
    }

    private static class ActiveSession {
        private final CourseSession courseSession;
        private final int readerId;
        private volatile boolean closed = false;
        private Timer timer;

        public ActiveSession(CourseSession courseSession, int readerId) {
            this.courseSession = courseSession;
            this.readerId = readerId;
        }

        public void setTimer(Timer timer) {
            this.timer = timer;
        }

        public boolean isClosed() {
            return closed;
        }

        public void close() {
            closed = true;
        }

        public CourseSession getCourseSession() {
            return courseSession;
        }
    }
}
