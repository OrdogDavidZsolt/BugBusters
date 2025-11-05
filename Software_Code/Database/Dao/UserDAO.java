package Dao;

import JPAUtil.JPAUtil;
import Model.User;
import jakarta.persistence.EntityManager;
import java.util.List;

public interface UserDAO {

    EntityManager em = JPAUtil.getEntityManager();

    void saveUser(User user);

    User getUserById(Long id);

    User getUserByEmail(String email);

    User getUserByCardId(String cardId);

    User getUserByNeptunCode(String neptunCode);

    List<User> getAllUsers();

    void updateUser(User user);

    void deleteUser(User user);
}
