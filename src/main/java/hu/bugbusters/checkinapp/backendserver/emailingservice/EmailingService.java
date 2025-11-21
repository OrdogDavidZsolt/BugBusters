package hu.bugbusters.checkinapp.backendserver.emailingservice;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import hu.bugbusters.checkinapp.database.model.Attendance;
import hu.bugbusters.checkinapp.database.model.CourseSession;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailingService {

    private final JavaMailSender mailSender;

    public void sendAttendanceCsv(String toEmail, CourseSession session, List<Attendance> attendanceList) {
        try {
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("Név,Neptun Kód,Érkezés ideje,Státusz,Megjegyzés\n");

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            for (Attendance att : attendanceList) {
                csvContent.append(att.getStudent().getName()).append(",");
                csvContent.append(att.getStudent().getNeptunCode()).append(",");

                String time = (att.getScannedAt() != null) ? att.getScannedAt().format(timeFormatter) : "-";
                csvContent.append(time).append(",");

                csvContent.append(att.getStatus()).append(",");

                String note = (att.getNote() != null) ? att.getNote().replace(",", " ") : "";
                csvContent.append(note).append("\n");
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Jelenléti ív export: " + session.getCourse().getName());
            helper.setText("Tisztelt Tanár Úr/Nő!\n\nMellékelten küldjük a kért jelenléti ívet CSV formátumban.\n\nÜdvözlettel,\nRFID Rendszer");

            byte[] csvBytes = csvContent.toString().getBytes(StandardCharsets.UTF_8);
            ByteArrayResource resource = new ByteArrayResource(csvBytes);

            String filename = "jelenleti_" + session.getDate() + ".csv";
            helper.addAttachment(filename, resource);

            mailSender.send(message);
            System.out.println("Email sikeresen elküldve ide: " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Hiba az email küldésekor");
        }
    }
}