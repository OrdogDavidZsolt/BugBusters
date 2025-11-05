package Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_sessions")
public class CourseSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Attendance> attendanceList = new ArrayList<>();

    public CourseSession() {
    }

    public CourseSession(Course course, LocalDate date, LocalDateTime startTime, LocalDateTime endTime) {
        this.course = course;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public List<Attendance> getAttendanceList() { return attendanceList; }
    public void setAttendanceList(List<Attendance> attendanceList) { this.attendanceList = attendanceList; }

    @Override
    public String toString() {
        return "CourseSession{" +
                "id=" + id +
                ", course=" + course +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", attendanceList=" + attendanceList +
                '}';
    }
}

