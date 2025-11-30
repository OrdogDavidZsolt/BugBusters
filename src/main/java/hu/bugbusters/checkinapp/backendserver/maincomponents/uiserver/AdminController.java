package hu.bugbusters.checkinapp.backendserver.maincomponents.uiserver;

import hu.bugbusters.checkinapp.backendserver.dto.ReaderDeviceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hu.bugbusters.checkinapp.backendserver.maincomponents.hwconnection.HW_Connection;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/readers")
    public ResponseEntity<List<ReaderDeviceDTO>> getReaders() {
        // A HW_Connection.getDeviceList() már a DTO-k listáját adja vissza
        return ResponseEntity.ok(HW_Connection.getDeviceList());
    }

    // Csak a nevet frissítjük, az ID a path-ból jön (pl. "001")
    @PutMapping("/device/{id}")
    public ResponseEntity<Void> updateDevice(@PathVariable String id, @RequestBody ReaderDeviceDTO dto) {
        // A dto.getName() tartalmazza az új nevet
        boolean success = HW_Connection.updateDeviceName(id, dto.getName());
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/db-link")
    public ResponseEntity<Map<String, String>> getDatabaseLink() {
        Map<String, String> response = Map.of("url", "http://localhost:8080/h2-console");
        return ResponseEntity.ok(response);
    }
}