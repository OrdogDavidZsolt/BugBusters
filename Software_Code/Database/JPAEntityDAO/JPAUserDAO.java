package JPAEntityDAO;

import Dao.UserDAO;
import JPAUtil.JPAUtil;
import Model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JPAUserDAO implements UserDAO {

    private final EntityManager entityManager = JPAUtil.getEntityManager();

    @Override
    public void saveUser(User user) {
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

    @Override
    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User getUserByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);

        List<User> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public User getUserByCardId(String cardId) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.cardId = :cardId", User.class);
        query.setParameter("cardId", cardId);

        List<User> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public User getUserByNeptunCode(String neptunCode) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.neptunCode = :neptunCode", User.class);
        query.setParameter("neptunCode", neptunCode);

        List<User> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<User> getAllUsers() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    @Override
    public void updateUser(User user) {
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteUser(User user) {
        entityManager.getTransaction().begin();
        entityManager.remove(user);
        entityManager.getTransaction().commit();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
