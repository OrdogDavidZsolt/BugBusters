package Model;

import jakarta.persistence.*;

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

    public User() {
    }

    public User(String name, String cardId, String neptunCode, UserRole role) {
        this.name = name;
        this.cardId = cardId;
        this.neptunCode = neptunCode;
        this.role = role;
    }

    public User(String name, String email, String cardId, String neptunCode, UserRole role, String hashedPassword) {
        this.name = name;
        this.email = email;
        this.cardId = cardId;
        this.neptunCode = neptunCode;
        this.role = role;
        this.hashedPassword = hashedPassword;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }
    public String getNeptunCode() { return neptunCode; }
    public void setNeptunCode(String neptunCode) { this.neptunCode = neptunCode; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

    @PrePersist
    @PreUpdate
    private void validateFields() {
        if (role == UserRole.TEACHER) {
            if (email == null || email.isBlank())
                throw new IllegalArgumentException("Teachers must have an email.");
            if (hashedPassword == null || hashedPassword.isBlank())
                throw new IllegalArgumentException("Teachers must have a hashed password.");
        } else {
            // Students shouldn’t have emails or passwords
            email = null;
            hashedPassword = null;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", cardId='" + cardId + '\'' +
                ", neptunCode='" + neptunCode + '\'' +
                ", role=" + role +
                '}';
    }
}

