package Dao;

import Model.User;
import java.util.List;

public interface UserDAO {

    void saveUser(User user);

    User getUserById(Long id);

    User getUserByEmail(String email);

    User getUserByCardId(String cardId);

    User getUserByNeptunCode(String neptunCode);

    List<User> getAllUsers();

    void updateUser(User user);

    void deleteUser(User user);
}
