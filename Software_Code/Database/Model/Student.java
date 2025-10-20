package Software_Code.Database.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "hallgato_lu")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hallgato_id")
    private Long id;

    @Column(name = "kartyaszam", nullable = false, length = 100)
    private String kartyaszam;

    @Column(name = "nev", nullable = false, length = 100)
    private String nev;

    @Column(name = "szak", length = 100)
    private String szak;

    @Column(name = "email", length = 100)
    private String email;

    public Student() {
    }

    public Student(String kartyaszam, String nev, String szak, String email) {
        this.kartyaszam = kartyaszam;
        this.nev = nev;
        this.szak = szak;
        this.email = email;
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKartyaszam() {  return kartyaszam;  }
    public void setKartyaszam(String kartyaszam) {  this.kartyaszam = kartyaszam;  }

    public String getNev() { return nev; }
    public void setNev(String nev) { this.nev = nev; }

    public String getSzak() { return szak; }
    public void setSzak(String szak) { this.szak = szak; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}