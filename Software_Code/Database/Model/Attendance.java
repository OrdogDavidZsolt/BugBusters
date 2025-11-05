package Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {

    public enum AttendanceStatus {
        PRESENT,
        LATE,
        ABSENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private CourseSession session;

    @Column(nullable = true) // ✅ allow NULL for absent
    private LocalDateTime scannedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    public Attendance() {}

    public Attendance(User student, CourseSession session, LocalDateTime scannedAt, AttendanceStatus status) {
        this.student = student;
        this.session = session;
        this.scannedAt = scannedAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public CourseSession getSession() { return session; }
    public void setSession(CourseSession session) { this.session = session; }
    public LocalDateTime getScannedAt() { return scannedAt; }
    public void setScannedAt(LocalDateTime scannedAt) { this.scannedAt = scannedAt; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", studentCardId=" + (student != null ? student.getCardId() : null) +
                ", sessionCourse=" + (session != null ? session.getCourse().getName() : null) +
                ", scannedAt=" + scannedAt +
                ", status=" + status +
                '}';
    }
}
