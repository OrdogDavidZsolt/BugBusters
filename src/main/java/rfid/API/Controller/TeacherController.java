package rfid.API.Controller;

import rfid.API.DTO.ClassDetailsDTO;
import rfid.API.DTO.CourseSummaryDTO;
import rfid.API.DTO.NoteUpdateDTO;
import rfid.API.DTO.StudentAttendanceDTO;
import rfid.DB.Model.Attendance;
import rfid.DB.Model.Course;
import rfid.DB.Model.CourseSession;
import rfid.DB.Model.User;
import rfid.DB.Repository.AttendanceRepository;
import rfid.DB.Repository.CourseRepository;
import rfid.DB.Repository.CourseSessionRepository;
import rfid.DB.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rfid.Service.EmailingService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "http://localhost:80")
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

    /**
     * Lekérdezi a bejelentkezett tanár összes kurzusát (a legördülő listához).
     */
    @GetMapping("/courses")
    public ResponseEntity<List<CourseSummaryDTO>> getMyCourses(Authentication authentication) {
        String email = authentication.getName(); // Spring Security adja a bejelentkezett user emailjét
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

    /**
     * Lekérdezi egy adott kurzus részleteit (a diáklistához).
     */
    @GetMapping("/course-details/{courseId}")
    public ResponseEntity<ClassDetailsDTO> getCourseDetails(@PathVariable Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Course course = courseOpt.get();

        // Keressük meg a kurzus legutóbbi óráját
        Optional<CourseSession> sessionOpt = courseSessionRepository.findTopByCourseOrderByIdDesc(course);
        if (sessionOpt.isEmpty()) {
            // Még nem volt óra ehhez a kurzushoz, de adjunk vissza egy üres lapot
            ClassDetailsDTO emptyDto = new ClassDetailsDTO();
            emptyDto.setName(course.getName());
            emptyDto.setTeacher(course.getTeacher().getName());
            emptyDto.setLocation("N/A");
            emptyDto.setDateTime("N/A");
            emptyDto.setStudents(List.of()); // Üres diáklista
            return ResponseEntity.ok(emptyDto);
        }
        CourseSession session = sessionOpt.get();

        // Keressük meg az órán résztvevő diákokat
        List<Attendance> attendances = attendanceRepository.findBySession(session);

        List<StudentAttendanceDTO> studentDTOs = attendances.stream().map(att -> {
            StudentAttendanceDTO dto = new StudentAttendanceDTO();
            dto.setAttendanceId(att.getId()); // Ezzel fogjuk tudni szerkeszteni/törölni
            dto.setName(att.getStudent().getName());
            dto.setCode(att.getStudent().getNeptunCode());
            dto.setTime(att.getScannedAt().format(timeFormatter));
            dto.setNote(att.getNote() != null ? att.getNote() : "");
            return dto;
        }).collect(Collectors.toList());

        // Állítsuk össze a teljes választ
        ClassDetailsDTO classDetailsDTO = new ClassDetailsDTO();
        classDetailsDTO.setSessionId(session.getId());
        classDetailsDTO.setName(course.getName());
        classDetailsDTO.setTeacher(course.getTeacher().getName());
        classDetailsDTO.setLocation(session.getLocation());
        classDetailsDTO.setDateTime(session.getStartTime().format(sessionFormatter));
        classDetailsDTO.setStudents(studentDTOs);

        return ResponseEntity.ok(classDetailsDTO);
    }

    /**
     * Frissíti egy adott jelenléti bejegyzéshez tartozó megjegyzést.
     */
    @PutMapping("/attendance/{attendanceId}/note")
    public ResponseEntity<Void> updateAttendanceNote(
            @PathVariable Long attendanceId,
            @RequestBody NoteUpdateDTO noteUpdateDTO) {

        // 1. Keressük meg a jelenléti rekordot az ID alapján
        Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);

        if (attendanceOpt.isEmpty()) {
            // Ha nincs ilyen rekord, 404 Not Found hibát küldünk
            return ResponseEntity.notFound().build();
        }

        // 2. Ha megvan, frissítsük a 'note' mezőt
        Attendance attendance = attendanceOpt.get();
        attendance.setNote(noteUpdateDTO.getNote());

        // 3. Mentsük el a változást az adatbázisba
        attendanceRepository.save(attendance);

        // 4. Küldjünk egy "200 OK" választ, hogy a mentés sikeres volt
        return ResponseEntity.ok().build();
    }

    /**
     * Töröl egy adott jelenléti bejegyzést (pl. egy diákot az óráról).
     */
    @DeleteMapping("/attendance/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId) {

        // 1. Ellenőrizzük, hogy létezik-e egyáltalán ez a rekord
        if (!attendanceRepository.existsById(attendanceId)) {
            // Ha nem létezik, 404 Not Found hibát küldünk
            return ResponseEntity.notFound().build();
        }

        // 2. Töröljük a rekordot az adatbázisból az ID alapján
        attendanceRepository.deleteById(attendanceId);

        // 3. Küldjünk egy "204 No Content" választ,
        // ez a szabványos válasz sikeres törlés esetén, ha nincs visszatérő adat.
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/export/{sessionId}")
    public ResponseEntity<Void> exportAttendanceToEmail(
            @PathVariable Long sessionId,
            @RequestBody String targetEmail) { // A body-ban várjuk az email címet

        // 1. Session keresése
        Optional<CourseSession> sessionOpt = courseSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CourseSession session = sessionOpt.get();

        // 2. Jelenléti lista lekérése
        List<Attendance> attendances = attendanceRepository.findBySession(session);

        // 3. Email küldése a Service segítségével
        // A targetEmail tartalmazhat macskakörrmöket vagy JSON formátumot, tisztítsuk meg
        String email = targetEmail.replace("\"", "").trim();

        emailingService.sendAttendanceCsv(email, session, attendances);

        return ResponseEntity.ok().build();
    }
}
