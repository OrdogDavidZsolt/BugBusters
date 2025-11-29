package hu.bugbusters.checkinapp.backendserver.maincomponents.hwconnection;

import hu.bugbusters.checkinapp.backendserver.dto.ReaderDeviceDTO;
import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class HW_Connection {

    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String CYAN   = "\u001B[36m";
    private static final String PREFIX = CYAN + ">> HW_Connection: " + RESET;

    // Devices map for Admin UI
    private static final Map<String, ReaderDeviceDTO> devices = new ConcurrentHashMap<>();

    // IP to ID map for quick lookup
    private static final Map<String, String> ipToIdMap = new ConcurrentHashMap<>();

    private static int nextReaderId = 1;
    private static final int PORT = 54321;
    private static final int THREAD_POOL_SIZE = 50;
    private static final int TOTAL_RECORD_SIZE = 19;

    // --- SPRING INJECTION FIX ---
    // We store the repository in a static field so the static start method can access it
    private static UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        HW_Connection.userRepository = userRepository;
    }
    // ----------------------------

    public static void start_HW_Server() {
        new Thread(() -> {
            ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println(PREFIX + "HW Szerver elindult a " + PORT + " porton...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    // Pass the static repository instance to the handler manually
                    pool.execute(new ClientHandler(clientSocket, userRepository));
                }
            } catch (IOException e) {
                System.out.println(PREFIX + RED + "IOException: " + RESET + e.getMessage());
            }
        }).start();
    }

    public static int getPort() {
        return HW_Connection.PORT;
    }

    // --- ADMIN UI METHODS ---

    public static List<ReaderDeviceDTO> getDeviceList() {
        return new ArrayList<>(devices.values());
    }

    public static boolean updateDevice(String id, String name, String position, String type) {
        if (devices.containsKey(id)) {
            ReaderDeviceDTO device = devices.get(id);
            device.setName(name);
            device.setPosition(position);
            device.setType(type);
            return true;
        }
        return false;
    }

    // --- COMMAND SENDER ---

    public static void sendCommandToReader(String readerId, HW_Command command) {
        ReaderDeviceDTO device = devices.get(readerId);

        if (device == null || device.getIp() == null) {
            System.out.println(PREFIX + "Nincs ilyen olvasó vagy nincs IP címe: " + readerId);
            return;
        }

        String targetIp = device.getIp();

        try (Socket socket = new Socket(targetIp, PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            out.writeInt(command.getCode());
            out.flush();

            System.out.println(PREFIX + "Parancs elküldve [" + command + "] a(z) " + targetIp + " címre.");
        } catch (IOException e) {
            System.out.println(PREFIX + "Hiba az üzenet küldésekor: " + e.getMessage());
        }
    }

    // --- CLIENT HANDLER ---

    public static class ClientHandler implements Runnable {
        private Socket socket;
        private String deviceId;

        // No @Autowired here. We use the final field passed via constructor.
        private final UserRepository ur;

        public ClientHandler(Socket socket, UserRepository ur) {
            this.socket = socket;
            this.ur = ur;
        }

        @Override
        public void run() {
            String clientIP = socket.getInetAddress().getHostAddress();
            System.out.println(PREFIX + "Új kliens kapcsolódott: " + clientIP);

            try (InputStream in = socket.getInputStream();
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                byte[] buffer = new byte[TOTAL_RECORD_SIZE];
                int bytesReadTotal = 0;

                while (bytesReadTotal < TOTAL_RECORD_SIZE) {
                    int bytesRead = in.read(buffer, bytesReadTotal, TOTAL_RECORD_SIZE - bytesReadTotal);
                    if (bytesRead == -1) {
                        System.out.println(PREFIX + "A kliens bontotta a kapcsolatot: " + clientIP);
                        return;
                    }
                    bytesReadTotal += bytesRead;
                }

                // Little-endian ID parse
                int readerIDInt = (buffer[3] & 0xFF) << 24 |
                        (buffer[2] & 0xFF) << 16 |
                        (buffer[1] & 0xFF) << 8  |
                        (buffer[0] & 0xFF);

                if (readerIDInt == Integer.MAX_VALUE) {
                    // New Configuration
                    this.deviceId = configureReader(clientIP);
                    out.write(this.deviceId.getBytes());
                    System.out.println(PREFIX + "Konfigurálva: " + this.deviceId + " (" + clientIP + ")");
                } else {
                    // Existing Device Logic
                    if (ipToIdMap.containsKey(clientIP)) {
                        this.deviceId = ipToIdMap.get(clientIP);
                    } else {
                        // Re-register if server restarted
                        this.deviceId = String.format("DEV-%03d", readerIDInt);
                        registerDevice(this.deviceId, clientIP);
                    }

                    // Parse UID
                    int uidOffset = 9;
                    StringBuilder uidHex = new StringBuilder();
                    for (int i = 0; i < 10; i++) {
                        uidHex.append(String.format("%02X", buffer[uidOffset + i]));
                    }

                    processUID(uidHex.toString());
                }

                setDeviceStatus(this.deviceId, true);

            } catch (IOException e) {
                System.out.println(PREFIX + "Hiba a kliens kommunikációban (" + clientIP + "): " + e.getMessage());
            } finally {
                if (this.deviceId != null) {
                    setDeviceStatus(this.deviceId, false);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        private String configureReader(String ip) {
            if (ipToIdMap.containsKey(ip)) {
                String existingId = ipToIdMap.get(ip);
                System.out.println(PREFIX + "Ismert olvasó újra csatlakozott: " + existingId);
                return existingId;
            }

            String newId = String.format("DEV-%03d", nextReaderId++);
            registerDevice(newId, ip);
            return newId;
        }

        private void registerDevice(String id, String ip) {
            ipToIdMap.put(ip, id);
            // Default values for new devices
            devices.putIfAbsent(id, new ReaderDeviceDTO(id, "Új Olvasó", "Nincs megadva", "RFID Reader", ip, true));
            // Update IP just in case
            devices.get(id).setIp(ip);
        }

        private void setDeviceStatus(String id, boolean online) {
            if (id != null && devices.containsKey(id)) {
                devices.get(id).setOnline(online);
            }
        }

        private void processUID(String uid) {
            System.out.println(PREFIX + "Feldolgozandó UID: " + uid + " (Eszköz: " + this.deviceId + ")");

            // Now 'ur' (UserRepository) is valid and not null
            if (ur != null) {
                Optional<User> result = ur.findByCardId(uid);

                if (result.isPresent()) {
                    User user = result.get();
                    System.out.println(PREFIX + "Kártya azonosítva: " + user.getName() + " (" + user.getRole() + ")");

                    if (user.getRole() == User.UserRole.TEACHER) {
                        // Teacher logic (start timer, etc.)
                        System.out.println(PREFIX + "Tanár belépés érzékelve.");
                    } else if (user.getRole() == User.UserRole.STUDENT) {
                        // Student logic (save attendance)
                        System.out.println(PREFIX + "Diák belépés érzékelve.");
                    }
                } else {
                    System.out.println(PREFIX + "Ismeretlen kártya.");
                }
            } else {
                System.err.println(PREFIX + "CRITICAL: UserRepository is null!");
            }
        }
    }

    public enum HW_Command {
        RED_LED_ON(1),
        RED_LED_OFF(2),
        GREEN_LED_ON(3),
        GREEN_LED_OFF(4),
        BLUE_LED_ON(5),
        BLUE_LED_OFF(6),
        RESET(7);

        private final int code;

        HW_Command(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}