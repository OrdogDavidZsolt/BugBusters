package hu.bugbusters.checkinapp.backendserver.maincomponents.services;

import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByCardId(String cardId) {
        return userRepository.findByCardId(cardId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByNeptunCode(String code) {
        return userRepository.findByNeptunCode(code);
    }
}
