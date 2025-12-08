package hu.bugbusters.checkinapp.database.repository;

import hu.bugbusters.checkinapp.backendserver.maincomponents.services.UserService;
import hu.bugbusters.checkinapp.database.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Test
    void testFindByCardId_Found() {
        User mockUser = new User();
        mockUser.setName("Teszt Elek");
        when(userRepository.findByCardId("123")).thenReturn(Optional.of(mockUser));
        Optional<User> result = userService.findByCardId("123");
        assertTrue(result.isPresent());
    }

    @Test
    void testFindByCardId_NotFound() {
        when(userRepository.findByCardId(anyString())).thenReturn(Optional.empty());
        Optional<User> result = userService.findByCardId("999");
        assertFalse(result.isPresent());
    }
}
