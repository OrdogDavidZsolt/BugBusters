package DB;

import Software_Code.Database.Model.*;
import Software_Code.Database.Dao.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication(scanBasePackages = "Software_Code.Database.Dao")
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(TeacherDao teacherRepo,
                                   StudentDao studentRepo,
                                   CourseDao courseRepo) {
        return args -> {
            // Example teachers
            Teacher t1 = new Teacher("CWERT6","Dr. Kovács Béla", "kovacs.bela@univ.hu");
            Teacher t2 = new Teacher("HB7654","Dr. Nagy Anna", "nagy.anna@univ.hu");
            teacherRepo.save(t1);
            teacherRepo.save(t2);

            // Example students
            Student s1 = new Student("S06ABA", "Tóth Dávid", "Informatika", "toth.david@univ.hu");
            Student s2 = new Student("G03GG4", "Kiss Júlia", "Gépészmérnök", "kiss.julia@univ.hu");
            Student s3 = new Student("HBM78O", "Horváth Péter", "Matematika", "horvath.peter@univ.hu");
            Student s4 = new Student("HGH78O", "Horváth Lajos", "Matematika", "horvath.lajos@univ.hu");
            studentRepo.save(s1);
            studentRepo.save(s2);
            studentRepo.save(s3);
            studentRepo.save(s4);

            // Example courses
            Course c1 = new Course("Programozás 1", t1);
            Course c2 = new Course("Adatbázis rendszerek", t2);
            Course c3 = new Course("Algoritmusok", t2);
            courseRepo.save(c1);
            courseRepo.save(c2);
            courseRepo.save(c3);

            System.out.println("✅ Sample data loaded.");
        };
    }
}
