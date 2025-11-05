package DB;

import Software_Code.Database.Dao.AttendanceDAO;
import Software_Code.Database.Dao.CourseDAO;
import Software_Code.Database.Dao.CourseSessionDAO;
import Software_Code.Database.Dao.UserDAO;
import Software_Code.Database.JPAEntityDAO.JPAAttendanceDAO;
import Software_Code.Database.JPAEntityDAO.JPACourseDAO;
import Software_Code.Database.JPAEntityDAO.JPACourseSessionDAO;
import Software_Code.Database.JPAEntityDAO.JPAUserDAO;
import Software_Code.Database.Manager.AttendanceManager;
import Software_Code.Database.Manager.CourseManager;
import Software_Code.Database.Manager.CourseSessionManager;
import Software_Code.Database.Manager.UserManager;

import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.sql.SQLException;

@EnableJpaRepositories(basePackages = "Software_Code.Database.Dao")
@EntityScan(basePackages = "Software_Code.Database.Model")
public class DB_Test {

    public static void main(String[] args) throws SQLException {
        startDatabase();
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

    private static void startDatabase() throws SQLException {
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
}
