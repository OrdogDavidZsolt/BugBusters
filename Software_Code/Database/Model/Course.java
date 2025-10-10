package Software_Code.Database.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int course_id;

    private int student_id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    public Course() {}

    public Course(int teacher_id, int student_id) {
        this.teacher = teacher;
        this.student_id = student_id;
    }

    public int getCourse_id() { return course_id; }

    public Teacher getTeacher() { return teacher; }

    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public int getStudent_id() { return student_id; }

    public void setStudent_id(int student_id) { this.student_id = student_id; }

    @Override
    public String toString() {
        return "Course{" +
                "course_id=" + getCourse_id() +
                ", teacher=" + (teacher != null ? teacher.getId() : null) +
                ", student_id=" + student_id +
                '}';
    }
}

