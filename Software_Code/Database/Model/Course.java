package Software_Code.Database.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "tantargy_lu")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tantargy_id")
    private Long id;

    @Column(name = "nev", nullable = false, length = 100)
    private String nev;

    @ManyToOne
    @JoinColumn(name = "tanar_id", referencedColumnName = "tanar_id")
    private Teacher tanar;

    public Course() {
    }

    public Course(String nev, Teacher tanar) {
        this.nev = nev;
        this.tanar = tanar;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNev() { return nev; }
    public void setNev(String nev) { this.nev = nev; }

    public Teacher getTanar() { return tanar; }
    public void setTanar(Teacher tanar) { this.tanar = tanar; }
}

