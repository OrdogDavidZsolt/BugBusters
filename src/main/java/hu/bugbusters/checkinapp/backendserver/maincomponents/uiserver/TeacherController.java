package hu.bugbusters.checkinapp.backendserver.maincomponents.uiserver;

import hu.bugbusters.checkinapp.backendserver.dto.ClassDetailsDTO;
import hu.bugbusters.checkinapp.backendserver.dto.CourseSummaryDTO;
import hu.bugbusters.checkinapp.backendserver.dto.NoteUpdateDTO;
import hu.bugbusters.checkinapp.backendserver.dto.StudentAttendanceDTO;
import hu.bugbusters.checkinapp.database.model.Attendance;
import hu.bugbusters.checkinapp.database.model.Course;
import hu.bugbusters.checkinapp.database.model.CourseSession;
import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.AttendanceRepository;
import hu.bugbusters.checkinapp.database.repository.CourseRepository;
import hu.bugbusters.checkinapp.database.repository.CourseSessionRepository;
import hu.bugbusters.checkinapp.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import hu.bugbusters.checkinapp.backendserver.emailingservice.EmailingService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private CourseSessionRepository courseSessionRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private EmailingService emailingService;

    private static final DateTimeFormatter sessionFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @GetMapping("/{teacherId}/courses")
    public List<Course> getCoursesByTeacher(@PathVariable Long teacherId) {
        return courseRepository.findByTeacher_Id(teacherId);
    }

    @GetMapping("/courses/{courseId}/sessions")
    public List<CourseSession> getSessionsByCourse(@PathVariable Long courseId) {
        return courseSessionRepository.findByCourse_Id(courseId);
    }

    @GetMapping("/sessions/{sessionId}/attendance")
    public List<Attendance> getAttendanceBySession(@PathVariable Long sessionId) {
        return attendanceRepository.findBySession_Id(sessionId);
    }

    //Lekérdezi a bejelentkezett tanár összes kurzusát (a legördülő listához).
    @GetMapping("/courses")
    public ResponseEntity<List<CourseSummaryDTO>> getMyCourses(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> teacherOpt = userRepository.findByEmail(email);

        if (teacherOpt.isEmpty() || teacherOpt.get().getRole() == User.UserRole.STUDENT) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Course> courses = courseRepository.findByTeacher(teacherOpt.get());

        List<CourseSummaryDTO> dtos = courses.stream().map(course -> {
            CourseSummaryDTO dto = new CourseSummaryDTO();
            dto.setId(course.getId());
            dto.setName(course.getName());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    //Lekérdezi egy adott kurzus részleteit (a diáklistához).
    @GetMapping("/course-details/{courseId}")
    public ResponseEntity<ClassDetailsDTO> getCourseDetails(@PathVariable Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Course course = courseOpt.get();

        Optional<CourseSession> sessionOpt = courseSessionRepository.findTopByCourseOrderByIdDesc(course);
        if (sessionOpt.isEmpty()) {
            ClassDetailsDTO emptyDto = new ClassDetailsDTO();
            emptyDto.setName(course.getName());
            emptyDto.setTeacher(course.getTeacher().getName());
            emptyDto.setLocation("N/A");
            emptyDto.setDateTime("N/A");
            emptyDto.setStudents(List.of());
            return ResponseEntity.ok(emptyDto);
        }
        CourseSession session = sessionOpt.get();

        List<Attendance> attendances = attendanceRepository.findBySession(session);

        List<StudentAttendanceDTO> studentDTOs = attendances.stream().map(att -> {
            StudentAttendanceDTO dto = new StudentAttendanceDTO();
            dto.setAttendanceId(att.getId());
            dto.setName(att.getStudent().getName());
            dto.setCode(att.getStudent().getNeptunCode());
            dto.setTime(att.getScannedAt().format(timeFormatter));
            dto.setNote(att.getNote() != null ? att.getNote() : "");
            return dto;
        }).collect(Collectors.toList());

        ClassDetailsDTO classDetailsDTO = new ClassDetailsDTO();
        classDetailsDTO.setSessionId(session.getId());
        classDetailsDTO.setName(course.getName());
        classDetailsDTO.setTeacher(course.getTeacher().getName());
        classDetailsDTO.setLocation(session.getLocation());
        classDetailsDTO.setDateTime(session.getStartTime().format(sessionFormatter));
        classDetailsDTO.setStudents(studentDTOs);

        return ResponseEntity.ok(classDetailsDTO);
    }

    //Frissíti egy adott jelenléti bejegyzéshez tartozó megjegyzést.
    @PutMapping("/attendance/{attendanceId}/note")
    public ResponseEntity<Void> updateAttendanceNote(
            @PathVariable Long attendanceId,
            @RequestBody NoteUpdateDTO noteUpdateDTO) {

        Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);

        if (attendanceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Attendance attendance = attendanceOpt.get();
        attendance.setNote(noteUpdateDTO.getNote());

        attendanceRepository.save(attendance);

        return ResponseEntity.ok().build();
    }

    //Töröl egy adott jelenléti bejegyzést (pl. egy diákot az óráról).
    @DeleteMapping("/attendance/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId) {

        if (!attendanceRepository.existsById(attendanceId)) {
            return ResponseEntity.notFound().build();
        }

        attendanceRepository.deleteById(attendanceId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/export/{sessionId}")
    public ResponseEntity<Void> exportAttendanceToEmail(
            @PathVariable Long sessionId,
            @RequestBody String targetEmail) {

        Optional<CourseSession> sessionOpt = courseSessionRepository.findById(sessionId);

        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CourseSession session = sessionOpt.get();

        List<Attendance> attendances = attendanceRepository.findBySession(session);

        String email = targetEmail.replace("\"", "").trim();

        emailingService.sendAttendanceCsv(email, session, attendances);

        return ResponseEntity.ok().build();
    }
}