package JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("br.com.fredericci.pu");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}

