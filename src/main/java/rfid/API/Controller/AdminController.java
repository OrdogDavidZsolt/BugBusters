package rfid.API.Controller;

import rfid.Service.HW_Connection; // Feltételezve, hogy a rfid.HW_Connection osztályod itt érhető el
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin") // Új, tiszta API útvonal az admin funkcióknak
public class AdminController {
    /**
     * Lekérdezi a csatlakoztatott kártyaolvasókat.
     * Ezt a végpontot fogja hívni az admin felület JavaScript kódja.
     */
    @GetMapping("/readers")
    public ResponseEntity<Map<String, String>> getReaderMap() {

        // Meghívjuk a meglévő statikus metódusodat a rfid.HW_Connection osztályból
        Map<String, String> readerData = HW_Connection.getReaders();

        // A Spring Boot automatikusan átalakítja a 'readerData' Map-et JSON formátumra
        return ResponseEntity.ok(readerData);
    }

    /**
     * Visszaadja az H2 adatbázis-konzol linkjét.
     */
    @GetMapping("/db-link")
    public ResponseEntity<Map<String, String>> getDatabaseLink() {

        // Egy egyszerű JSON objektumot küldünk vissza: {"url": "http://..."}
        Map<String, String> response = Map.of("url", "http://localhost:8080/h2-console");

        return ResponseEntity.ok(response);
    }
}
