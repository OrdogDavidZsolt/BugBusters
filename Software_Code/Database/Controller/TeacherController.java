package Controller;

import Model.*;
import Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final CourseRepository courseRepository;
    private final CourseSessionRepository courseSessionRepository;
    private final AttendanceRepository attendanceRepository;

    // 1️⃣ Tanár kurzusai
    @GetMapping("/{teacherId}/courses")
    public List<Course> getCoursesByTeacher(@PathVariable Long teacherId) {
        return courseRepository.findByTeacher_Id(teacherId);
    }

    // 2️⃣ Egy kurzushoz tartozó órák
    @GetMapping("/courses/{courseId}/sessions")
    public List<CourseSession> getSessionsByCourse(@PathVariable Long courseId) {
        return courseSessionRepository.findByCourse_Id(courseId);
    }

    // 3️⃣ Egy session jelenléti íve
    @GetMapping("/sessions/{sessionId}/attendance")
    public List<Attendance> getAttendanceBySession(@PathVariable Long sessionId) {
        return attendanceRepository.findBySession_Id(sessionId);
    }
}
