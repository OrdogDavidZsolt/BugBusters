package hu.bugbusters.checkinapp.backendserver.dbconnection;

import hu.bugbusters.checkinapp.database.model.Attendance;
import hu.bugbusters.checkinapp.database.model.Course;
import hu.bugbusters.checkinapp.database.model.CourseSession;
import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.AttendanceRepository;
import hu.bugbusters.checkinapp.database.repository.CourseRepository;
import hu.bugbusters.checkinapp.database.repository.CourseSessionRepository;
import hu.bugbusters.checkinapp.database.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class DB_Connection implements CommandLineRunner {

    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE   = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN   = "\u001B[36m";
    private static final String WHITE  = "\u001B[37m";
    private static final String PREFIX = PURPLE + ">> DB_Connection: " + RESET;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSessionRepository courseSessionRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public void startDatabase() {
        try
        {
            Server.createTcpServer("-tcp", "-web", "-ifNotExists").start();
            System.out.println("http://localhost:8080/h2-console");
            System.out.println(PREFIX + "Database running");
        }
        catch (SQLException e)
        {
            System.out.println(PREFIX + RED + "SQL Exception raised: " + RESET + e.getMessage());
        }
    }

    @Override
    public void run(String... args) throws Exception {
        /*// 1. Users
        User teacher = User.builder()
                .name("Prof. Smith")
                .email("smith@university.com")
                .role(User.UserRole.TEACHER)
                .cardId("100")
                .neptunCode("ABC123")
                .hashedPassword("password123")
                .build();

        User student = User.builder()
                .name("Alice")
                .role(User.UserRole.STUDENT)
                .cardId("101")
                .neptunCode("DEF456")
                .build();

        userRepository.save(teacher);
        userRepository.save(student);

        // 2. Course
        Course course = Course.builder()
                .name("Math 101")
                .teacher(teacher)
                .build();
        courseRepository.save(course);

        // 3. Course Session
        CourseSession session = CourseSession.builder()
                .course(course)
                .date(LocalDate.now())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();
        courseSessionRepository.save(session);

        // 4. Attendance
        Attendance attendance = Attendance.builder()
                .student(student)
                .session(session)
                .status(Attendance.AttendanceStatus.PRESENT)
                .scannedAt(LocalDateTime.now())
                .build();
        attendanceRepository.save(attendance);*/
    }
}
