package Software_Code.Database.Model;


import jakarta.persistence.*;

@Entity
@Table(name = "tanar_lu")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tanar_id")
    private Long id;

    @Column(name = "kartyaszam", nullable = false, length = 100)
    private String kartyaszam;

    @Column(name = "nev", nullable = false, length = 100)
    private String nev;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    public Teacher() {
    }

    public Teacher(String kartyaszam, String nev, String email) {
        this.kartyaszam = kartyaszam;
        this.nev = nev;
        this.email = email;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKartyaszam() {  return kartyaszam;  }
    public void setKartyaszam(String kartyaszam) {  this.kartyaszam = kartyaszam;  }

    public String getNev() { return nev; }
    public void setNev(String nev) { this.nev = nev; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
