package hu.bugbusters.checkinapp.database.repository;

import hu.bugbusters.checkinapp.backendserver.maincomponents.controller.Controller;
import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.model.User.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

@SpringBootTest
@ContextConfiguration(classes = Controller.class)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void testSaveUser() {
        User user = User.builder()
                .name("Sanyi")
                .email("sanyi@pelda.hu")
                .cardId("CARD_001")
                .neptunCode("SANYI1")
                .role(UserRole.STUDENT)
                .build();
        User savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
        Assertions.assertThat(savedUser.getName()).isEqualTo("Sanyi");
    }

    @Test
    void testFindUserById() {
        User user = User.builder()
                .name("Béla")
                .email("bela@pelda.hu")
                .cardId("CARD_002")
                .neptunCode("BELAB2")
                .role(UserRole.TEACHER)
                .build();
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getEmail()).isEqualTo("bela@pelda.hu");
    }

    @Test
    void testFindCustomMethods() {
        User user = User.builder()
                .name("Juli")
                .email("juli@pelda.hu")
                .cardId("CARD_003")
                .neptunCode("JULI03")
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(user);

        Optional<User> byEmail = userRepository.findByEmail("juli@pelda.hu");
        Assertions.assertThat(byEmail).isPresent();

        Assertions.assertThat(byEmail.get().getName()).isEqualTo("Juli");
        Optional<User> byCardId = userRepository.findByCardId("CARD_003");
        Assertions.assertThat(byCardId).isPresent();

        Assertions.assertThat(byCardId.get().getRole()).isEqualTo(UserRole.ADMIN);
        Optional<User> byNeptun = userRepository.findByNeptunCode("JULI03");
        Assertions.assertThat(byNeptun).isPresent();
    }

    @Test
    void testUpdateUser() {
        User user = User.builder()
                .name("Géza")
                .email("geza@pelda.hu")
                .cardId("CARD_004")
                .neptunCode("GEZA04")
                .role(UserRole.STUDENT)
                .build();
        User savedUser = userRepository.save(user);

        savedUser.setName("Géza Módosított");
        savedUser.setEmail("ujgeza@pelda.hu");
        User updatedUser = userRepository.save(savedUser);

        Assertions.assertThat(updatedUser.getName()).isEqualTo("Géza Módosított");
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("ujgeza@pelda.hu");
        Assertions.assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void testDeleteUser() {
        User user = User.builder()
                .name("Törlendő Ödön")
                .email("odon@pelda.hu")
                .cardId("CARD_005")
                .neptunCode("ODON05")
                .role(UserRole.STUDENT)
                .build();
        User savedUser = userRepository.save(user);

        Long userId = savedUser.getId();
        Assertions.assertThat(userRepository.findById(userId)).isPresent();

        userRepository.delete(savedUser);
        Optional<User> deletedUser = userRepository.findById(userId);
        Assertions.assertThat(deletedUser).isEmpty();
    }
}