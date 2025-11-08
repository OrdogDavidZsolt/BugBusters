package DB_Connection;

import Dao.*;
import Manager.*;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

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
    private UserDAO userDAO;

    @Autowired
    private CourseDAO courseDAO;

    @Autowired
    private CourseSessionDAO courseSessionDAO;

    @Autowired
    private AttendanceDAO attendanceDAO;

    public void startDatabase() {
        // H2 adatbázis indítása
        try {
            new Server().runTool("-tcp", "-web", "-ifNotExists");
        } catch (SQLException e) {
            System.out.println(PREFIX + RED + "SQL Exception raised: " + RESET + e.getMessage());
        }
        
        // DAO-k automatikusan injektálva vannak a Spring által
        UserManager userManager = new UserManager(userDAO);
        userManager.manage();

        CourseManager courseManager = new CourseManager(courseDAO);
        courseManager.manage();

        CourseSessionManager courseSessionManager = new CourseSessionManager(courseSessionDAO);
        courseSessionManager.manage();

        AttendanceManager attendanceManager = new AttendanceManager(attendanceDAO);
        attendanceManager.manage();
        
        System.out.println(PREFIX + "Database running");
    }
}
