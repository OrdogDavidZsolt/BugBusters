package Software_Code.Database.Model;


import jakarta.persistence.*;

@Entity
@Table(name = "teacher")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int teacher_id;

    private String name;
    private String email;

    public Teacher() {}

    public Teacher(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public int getId() { return teacher_id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacher_id=" + getId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
