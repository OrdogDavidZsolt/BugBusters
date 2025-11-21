package hu.bugbusters.checkinapp.database.init;

import hu.bugbusters.checkinapp.database.model.Attendance;
import hu.bugbusters.checkinapp.database.model.Course;
import hu.bugbusters.checkinapp.database.model.CourseSession;
import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.AttendanceRepository;
import hu.bugbusters.checkinapp.database.repository.CourseRepository;
import hu.bugbusters.checkinapp.database.repository.CourseSessionRepository;
import hu.bugbusters.checkinapp.database.repository.UserRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month; // Új import a konkrét hónap megadásához
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PopulateDatabase {

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            CourseRepository courseRepository,
            CourseSessionRepository courseSessionRepository,
            AttendanceRepository attendanceRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            System.out.println("Initializing database with extended test data...");

            // 1. LÉPÉS: Felhasználók (Admin + Tanárok)
            User admin = User.builder().name("Admin User").email("admin@test.com").cardId("ADMIN_CARD").neptunCode("ADMINN").role(User.UserRole.ADMIN).hashedPassword(passwordEncoder.encode("admin123")).build();
            userRepository.save(admin);
            User teacher1 = User.builder().name("Ördög Dávid Zsolt").email("ordogdavid2002@gmail.com").cardId("T_CARD_1").neptunCode("TEACH1").role(User.UserRole.TEACHER).hashedPassword(passwordEncoder.encode("123")).build();
            userRepository.save(teacher1);
            User teacher2 = User.builder().name("Dr. Nagy Anna").email("anna@test.com").cardId("T_CARD_2").neptunCode("TEACH2").role(User.UserRole.TEACHER).hashedPassword(passwordEncoder.encode("pass123")).build();
            userRepository.save(teacher2);

            // 2. LÉPÉS: Diákok (20 db)
            List<User> students = new ArrayList<>();
            // Csoport 1 (1-10)
            students.add(User.builder().name("Kiss Anna").cardId("S_1").neptunCode("ABC001").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Nagy Péter").cardId("S_2").neptunCode("ABC002").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Szabó Márta").cardId("S_3").neptunCode("ABC003").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Horváth László").cardId("S_4").neptunCode("ABC004").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Tóth Eszter").cardId("S_5").neptunCode("ABC005").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Farkas Viktória").cardId("S_6").neptunCode("ABC006").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Papp Zoltán").cardId("S_7").neptunCode("ABC007").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Lakatos Réka").cardId("S_8").neptunCode("ABC008").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Juhász Bence").cardId("S_9").neptunCode("ABC009").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Németh Dóra").cardId("S_10").neptunCode("ABC010").role(User.UserRole.STUDENT).build());
            // Csoport 2 (11-20)
            students.add(User.builder().name("Balogh Tamás").cardId("S_11").neptunCode("DEF011").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Kelemen Zsófia").cardId("S_12").neptunCode("DEF012").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Simon Gábor").cardId("S_13").neptunCode("DEF013").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Varga Bálint").cardId("S_14").neptunCode("DEF014").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Molnár Kinga").cardId("S_15").neptunCode("DEF015").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Fekete Ádám").cardId("S_16").neptunCode("DEF016").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Orosz Luca").cardId("S_17").neptunCode("DEF017").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Vass Richárd").cardId("S_18").neptunCode("DEF018").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Novák Csaba").cardId("S_19").neptunCode("DEF019").role(User.UserRole.STUDENT).build());
            students.add(User.builder().name("Szekeres Júlia").cardId("S_20").neptunCode("DEF020").role(User.UserRole.STUDENT).build());

            userRepository.saveAll(students);

            // 3. LÉPÉS: Kurzusok
            Course c1 = Course.builder().name("Webfejlesztés").teacher(teacher1).build();
            Course c2 = Course.builder().name("Adatbázisrendszerek").teacher(teacher1).build();
            Course c3 = Course.builder().name("Algoritmusok").teacher(teacher2).build();
            Course c4 = Course.builder().name("Szoftverfejlesztés").teacher(teacher2).build();

            courseRepository.saveAll(List.of(c1, c2, c3, c4));

            // 4. LÉPÉS: Órák (Sessions) és Jelenlétek
            List<CourseSession> sessions = new ArrayList<>();
            List<Attendance> attendances = new ArrayList<>();

            // --- Webfejlesztés (c1) ---

            // 1. óra (Múlt hét)
            CourseSession s1_1 = CourseSession.builder().course(c1).date(LocalDate.now().minusWeeks(1)).startTime(LocalDateTime.now().minusWeeks(1).withHour(14)).endTime(LocalDateTime.now().minusWeeks(1).withHour(16)).location("IK-101").build();
            sessions.add(s1_1);
            attendances.add(Attendance.builder().session(s1_1).student(students.get(0)).status(Attendance.AttendanceStatus.PRESENT).scannedAt(s1_1.getStartTime().plusMinutes(2)).build());
            attendances.add(Attendance.builder().session(s1_1).student(students.get(1)).status(Attendance.AttendanceStatus.LATE).scannedAt(s1_1.getStartTime().plusMinutes(15)).note("Késett").build());
            attendances.add(Attendance.builder().session(s1_1).student(students.get(2)).status(Attendance.AttendanceStatus.PRESENT).scannedAt(s1_1.getStartTime().plusMinutes(5)).build());
            attendances.add(Attendance.builder().session(s1_1).student(students.get(3)).status(Attendance.AttendanceStatus.ABSENT).build());

            // 2. óra (Ma)
            CourseSession s1_2 = CourseSession.builder().course(c1).date(LocalDate.now()).startTime(LocalDateTime.now().minusHours(1)).endTime(LocalDateTime.now().plusHours(1)).location("IK-101").build();
            sessions.add(s1_2);
            for(int i=0; i<8; i++) {
                attendances.add(Attendance.builder().session(s1_2).student(students.get(i)).status(Attendance.AttendanceStatus.PRESENT).scannedAt(s1_2.getStartTime().plusMinutes(i)).build());
            }

            // 3. óra (Jövő hét)
            CourseSession s1_3 = CourseSession.builder().course(c1).date(LocalDate.now().plusWeeks(1)).startTime(LocalDateTime.now().plusWeeks(1).withHour(14)).endTime(LocalDateTime.now().plusWeeks(1).withHour(16)).location("IK-101").build();
            sessions.add(s1_3);

            // --- ÚJ ÓRA: 2025.11.26 14:11 - Webfejlesztés ---
            LocalDateTime specificStart = LocalDateTime.of(2025, Month.NOVEMBER, 26, 14, 11);
            LocalDateTime specificEnd = specificStart.plusHours(2);

            CourseSession s1_new = CourseSession.builder()
                    .course(c1)
                    .date(specificStart.toLocalDate())
                    .startTime(specificStart)
                    .endTime(specificEnd)
                    .location("IK-105")
                    .build();
            sessions.add(s1_new);

            // Adjunk hozzá diákokat ehhez az új órához is (pl. 15 diákot)
            for(int i=0; i<15; i++) {
                // Véletlenszerű érkezési idők az óra kezdete után 0-10 perccel
                LocalDateTime scannedAt = specificStart.plusMinutes(i % 10);
                Attendance.AttendanceStatus status = (i % 5 == 0) ? Attendance.AttendanceStatus.LATE : Attendance.AttendanceStatus.PRESENT;
                String note = (status == Attendance.AttendanceStatus.LATE) ? "Késés" : "";

                attendances.add(Attendance.builder()
                        .session(s1_new)
                        .student(students.get(i))
                        .status(status)
                        .scannedAt(scannedAt)
                        .note(note)
                        .build());
            }


            // --- Adatbázisok (c2) - 2 óra ---
            CourseSession s2_1 = CourseSession.builder().course(c2).date(LocalDate.now().minusDays(1)).startTime(LocalDateTime.now().minusDays(1).withHour(10)).endTime(LocalDateTime.now().minusDays(1).withHour(12)).location("IK-203").build();
            sessions.add(s2_1);
            for(int i=5; i<15; i++) {
                attendances.add(Attendance.builder().session(s2_1).student(students.get(i)).status(Attendance.AttendanceStatus.PRESENT).scannedAt(s2_1.getStartTime().plusMinutes(i-4)).build());
            }

            CourseSession s2_2 = CourseSession.builder().course(c2).date(LocalDate.now()).startTime(LocalDateTime.now().withHour(8)).endTime(LocalDateTime.now().withHour(10)).location("IK-203").build();
            sessions.add(s2_2);
            for(int i=10; i<20; i++) {
                Attendance.AttendanceStatus status = (i % 5 == 0) ? Attendance.AttendanceStatus.LATE : Attendance.AttendanceStatus.PRESENT;
                String note = (status == Attendance.AttendanceStatus.LATE) ? "Busz késett" : "";
                attendances.add(Attendance.builder().session(s2_2).student(students.get(i)).status(status).scannedAt(s2_2.getStartTime().plusMinutes(i%10)).note(note).build());
            }

            // --- Algoritmusok (c3 - Teacher2) - 1 óra ---
            CourseSession s3_1 = CourseSession.builder().course(c3).date(LocalDate.now()).startTime(LocalDateTime.now().minusHours(3)).endTime(LocalDateTime.now().minusHours(1)).location("Lovarda").build();
            sessions.add(s3_1);
            for(User s : students) {
                attendances.add(Attendance.builder().session(s3_1).student(s).status(Attendance.AttendanceStatus.PRESENT).scannedAt(s3_1.getStartTime().plusMinutes(1)).build());
            }

            // Mentés
            courseSessionRepository.saveAll(sessions);
            attendanceRepository.saveAll(attendances);

            System.out.println("Adatbázis feltöltve: " + sessions.size() + " óra és " + attendances.size() + " jelenléti adat.");
        };
    }
}