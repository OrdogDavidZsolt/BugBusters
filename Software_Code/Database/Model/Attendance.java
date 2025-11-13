package Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "attendance")
public class Attendance {

    public enum AttendanceStatus {
        PRESENT,
        LATE,
        ABSENT
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private CourseSession session;

    @Column(nullable = true)
    private LocalDateTime scannedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;
}
