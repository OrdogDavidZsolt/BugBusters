package Software_Code.Database.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int course_id;

    private int teacher_id;
    private int student_id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    public Course() {}

    public Course(int course_id, int teacher_id, int student_id) {
        this.course_id = course_id;
        this.teacher_id = teacher_id;
        this.student_id = student_id;
    }

    public int getCourse_id() { return course_id; }

    public void setCourse_id(int course_id) { this.course_id = course_id; }

    public int getTeacher_id() { return teacher_id; }

    public void setTeacher_id(int teacher_id) { this.teacher_id = teacher_id; }

    public int getStudent_id() { return student_id; }

    public void setStudent_id(int student_id) { this.student_id = student_id; }

    @Override
    public String toString() {
        return "Course{" +
                "course_id=" + course_id +
                ", teacher_id=" + teacher_id +
                ", student_id=" + student_id +
                '}';
    }
}

