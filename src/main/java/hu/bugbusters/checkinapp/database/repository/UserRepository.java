package hu.bugbusters.checkinapp.database.repository;

import hu.bugbusters.checkinapp.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCardId(String cardId);
    Optional<User> findByNeptunCode(String neptunCode);
}