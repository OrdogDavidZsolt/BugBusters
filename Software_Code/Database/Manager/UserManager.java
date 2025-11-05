package Software_Code.Database.Manager;

import Software_Code.Database.Dao.UserDAO;
import Software_Code.Database.JPAEntityDAO.JPAUserDAO;
import Software_Code.Database.Model.User;

public class UserManager {
    UserDAO userDAO = new JPAUserDAO();

    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void manage() {
        System.out.println("\n=== Testing User DAO ===");

        // Students (no email, no password)
        User u1 = new User("Dávid", "11", "SN6ABA", User.UserRole.STUDENT);
        User u2 = new User("Zalán", "22", "SM4ACA", User.UserRole.STUDENT);
        User u3 = new User("Anna", "33", "SN9BBA", User.UserRole.STUDENT);
        User u4 = new User("Bence", "44", "SM7CAA", User.UserRole.STUDENT);
        User u5 = new User("Réka", "55", "SN2HAA", User.UserRole.STUDENT);
        User u6 = new User("Lilla", "66", "SM3JAA", User.UserRole.STUDENT);

        // Teachers (have email + password)
        User u7 = new User("Péter", "teacher.peter@univ.com", "77", "TN2DDA", User.UserRole.TEACHER, "teach123");
        User u8 = new User("Eszter", "eszter.nagy@univ.com", "88", "TN3EAA", User.UserRole.TEACHER, "teachme");
        User u9 = new User("Gábor", "gabor.kiss@univ.com", "99", "TN5FAA", User.UserRole.TEACHER, "secure123");

        // Admin (if applicable in your UserRole enum)
        //User u10 = new User("Tamás", "tamas.admin@univ.com", "10101010101", "AD1AAA", User.UserRole.ADMIN, "rootadmin");

        // Save all
        userDAO.saveUser(u1);
        userDAO.saveUser(u2);
        userDAO.saveUser(u3);
        userDAO.saveUser(u4);
        userDAO.saveUser(u5);
        userDAO.saveUser(u6);
        userDAO.saveUser(u7);
        userDAO.saveUser(u8);
        userDAO.saveUser(u9);
        //userDAO.saveUser(u10);

        System.out.println("\n--- All Users ---");
        for (User u : userDAO.getAllUsers()) {
            System.out.println(u);
        }

        System.out.println("\n--- Get by email ---");
        System.out.println(userDAO.getUserByEmail("gabor.kiss@univ.com"));

        System.out.println("\n--- Get by Neptun code ---");
        System.out.println(userDAO.getUserByNeptunCode("SM3JAA"));
    }

}
