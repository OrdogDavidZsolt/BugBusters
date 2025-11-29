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
        // Return the full list of device objects
        return ResponseEntity.ok(HW_Connection.getDeviceList());
    }

    @PutMapping("/device/{id}")
    public ResponseEntity<Void> updateDevice(@PathVariable String id, @RequestBody ReaderDeviceDTO dto) {
        boolean success = HW_Connection.updateDevice(id, dto.getName(), dto.getPosition(), dto.getType());
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/db-link")
    public ResponseEntity<Map<String, String>> getDatabaseLink() {
        Map<String, String> response = Map.of("url", "http://localhost:8080/h2-console");
        return ResponseEntity.ok(response);
    }
}