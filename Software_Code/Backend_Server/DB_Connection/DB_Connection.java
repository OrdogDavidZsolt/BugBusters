package DB_Connection;

import Dao.*;
import JPAEntityDAO.*;
import Manager.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.h2.tools.Server;

public class DB_Connection {
    private static final String URL = "jdbc:mysql://localhost:3306/example"; //online adatbázis linkje és belépési infók átírása
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void startDatabase() throws SQLException {
        new Server().runTool("-tcp", "-web", "-ifNotExists");
        UserDAO userDAO = new JPAUserDAO();
        CourseDAO courseDAO = new JPACourseDAO();
        CourseSessionDAO courseSessionDAO = new JPACourseSessionDAO();
        AttendanceDAO attendanceDAO = new JPAAttendanceDAO();

        UserManager userManager = new UserManager(userDAO);
        userManager.manage();
        CourseManager courseManager = new CourseManager(courseDAO);
        courseManager.manage();
        CourseSessionManager courseSessionManager = new CourseSessionManager(courseSessionDAO);
        courseSessionManager.manage();
        AttendanceManager attendanceManager = new AttendanceManager(attendanceDAO);
        attendanceManager.manage();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
