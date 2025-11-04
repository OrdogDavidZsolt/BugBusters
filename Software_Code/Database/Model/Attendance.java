package Software_Code.Database.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private CourseSession session;

    @Column(nullable = false)
    private LocalDateTime scannedAt;

    // Pl. késés logika miatt
    @Column(nullable = false)
    private boolean late;

    public Attendance() {
    }

    public Attendance(User student, CourseSession session, LocalDateTime scannedAt, boolean late) {
        this.student = student;
        this.session = session;
        this.scannedAt = scannedAt;
        this.late = late;
    }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public CourseSession getSession() { return session; }
    public void setSession(CourseSession session) { this.session = session; }
    public LocalDateTime getScannedAt() { return scannedAt; }
    public void setScannedAt(LocalDateTime scannedAt) { this.scannedAt = scannedAt; }
    public boolean isLate() { return late; }
    public void setLate(boolean late) { this.late = late; }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", studentId=" + (student != null ? student.getCardId() : null) +
                ", sessionId=" + (session != null ? session.getCourse() : null) +
                ", scannedAt=" + scannedAt +
                ", late=" + late +
                '}';
    }
}

