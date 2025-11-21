package hu.bugbusters.checkinapp.database.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"cardId"}),
        @UniqueConstraint(columnNames = {"neptunCode"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cardId;

    @Column(nullable = false, unique = true, length = 6)
    private String neptunCode;

    public enum UserRole {
        TEACHER, STUDENT, ADMIN
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String hashedPassword;
}