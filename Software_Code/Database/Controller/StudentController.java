package Software_Code.Database.Controller;

import Software_Code.Database.Model.Student;
import Software_Code.Database.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Student not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.updateStudent(id, student)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Student not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id)
                ? ResponseEntity.ok("Student deleted")
                : ResponseEntity.status(404).body("Student not found");
    }

    @GetMapping("/kartyaszam/{kartyaszam}")
    public ResponseEntity<?> getByKartyaszam(@PathVariable String kartyaszam) {
        return studentService.getByKartyaszam(kartyaszam)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Student not found"));
    }

    @GetMapping("/szak/{szak}")
    public ResponseEntity<List<Student>> getBySzak(@PathVariable String szak) {
        return ResponseEntity.ok(studentService.getBySzak(szak));
    }

    @GetMapping("/search/{nev}")
    public ResponseEntity<List<Student>> searchByName(@PathVariable String nev) {
        return ResponseEntity.ok(studentService.searchByName(nev));
    }
}

