package Software_Code.Database.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int student_id;

    private String name;
    private String major;
    private String email;

    public Student() {}

    //Constructor for retrieving from DB (with id)
    public Student(int id, String name, String major, String email) {
        this.student_id = id;
        this.name = name;
        this.major = major;
        this.email = email;
    }

    public int getId() { return student_id; }

    public void setId(int id) { this.student_id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getMajor() { return major; }

    public void setMajor(String major) { this.major = major; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Student{" +
                "student_id=" + student_id +
                ", name='" + name + '\'' +
                ", major='" + major + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
