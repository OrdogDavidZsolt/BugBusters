package Software_Code.Database.Controller;

import Software_Code.Database.Model.Course;
import Software_Code.Database.Model.Teacher;
import Software_Code.Database.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Course not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return courseService.updateCourse(id, course)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Course not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return courseService.deleteCourse(id)
                ? ResponseEntity.ok("Course deleted")
                : ResponseEntity.status(404).body("Course not found");
    }

    @GetMapping("/search/{nev}")
    public ResponseEntity<List<Course>> searchByName(@PathVariable String nev) {
        return ResponseEntity.ok(courseService.searchByName(nev));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Course>> getByTeacherId(@PathVariable Long teacherId) {
        return ResponseEntity.ok(courseService.findByTeacherId(teacherId));
    }
}
