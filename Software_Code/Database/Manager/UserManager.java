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
        System.out.println("=== Testing User DAO ===");

        User u1 = new User("Dávid", "11111111111", "SN6ABA", User.UserRole.STUDENT);
        User u2 = new User("Zalán", "22222222222", "SM4ACA", User.UserRole.STUDENT);
        User u3 = new User("Péter", "teacher@univ.com", "33333333333", "TN2DDA", User.UserRole.TEACHER, "password");

        userDAO.saveUser(u1);
        userDAO.saveUser(u2);
        userDAO.saveUser(u3);

        System.out.println("\n--- All Users ---");
        for (User u : userDAO.getAllUsers()) {
            System.out.println(u);
        }

        System.out.println("\n--- Get by email ---");
        System.out.println(userDAO.getUserByEmail("stud1@univ.com"));

        System.out.println("\n--- Get by Neptun code ---");
        System.out.println(userDAO.getUserByNeptunCode("TN2DDA"));
    }

}
