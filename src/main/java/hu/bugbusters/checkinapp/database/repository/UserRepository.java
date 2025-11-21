package hu.bugbusters.checkinapp.database.repository;

import hu.bugbusters.checkinapp.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
