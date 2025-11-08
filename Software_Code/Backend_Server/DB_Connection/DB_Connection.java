package DB_Connection;

import Dao.*;
import Manager.*;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class DB_Connection {

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
            System.out.println(">> DB_Connection: SQL Exception raised: " + e.getMessage());
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
        
        System.out.println(">> DB_Connection: Database running");
    }
}
