package hu.bugbusters.checkinapp.backendserver.maincomponents.uiserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.bugbusters.checkinapp.backendserver.maincomponents.hwconnection.HW_Connection;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/readers")
    public ResponseEntity<Map<String, String>> getReaderMap() {

        // Meghívjuk a meglévő statikus metódusodat a rfid.HW_Connection osztályból
        Map<String, String> readerData = HW_Connection.getReaders();

        // A Spring Boot automatikusan átalakítja a 'readerData' Map-et JSON formátumra
        return ResponseEntity.ok(readerData);
    }

    @GetMapping("/db-link")
    public ResponseEntity<Map<String, String>> getDatabaseLink() {
        Map<String, String> response = Map.of("url", "http://localhost:8080/h2-console");
        return ResponseEntity.ok(response);
    }
}