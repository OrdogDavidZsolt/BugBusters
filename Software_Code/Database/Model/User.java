package Model;

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

    // Email is optional — only required for teachers
    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cardId; // RFID kártya azonosító

    @Column(nullable = false, unique = true, length = 6)
    private String neptunCode; // NEPTUN kód (6 karakter)

    public enum UserRole {
        TEACHER, STUDENT
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // STUDENT or TEACHER

    // Only teachers will have this set
    private String hashedPassword;
}