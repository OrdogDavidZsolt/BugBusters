package hu.bugbusters.checkinapp.backendserver.maincomponents.dbconnection;

import hu.bugbusters.checkinapp.database.repository.AttendanceRepository;
import hu.bugbusters.checkinapp.database.repository.CourseRepository;
import hu.bugbusters.checkinapp.database.repository.CourseSessionRepository;
import hu.bugbusters.checkinapp.database.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class DB_Connection {

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
}
