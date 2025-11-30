package hu.bugbusters.checkinapp.backendserver.maincomponents.hwconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import hu.bugbusters.checkinapp.backendserver.dto.ReaderDeviceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.UserRepository;


@Component
public class HW_Connection
{
    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE   = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN   = "\u001B[36m";
    private static final String WHITE  = "\u001B[37m";
    private static final String PREFIX = CYAN + ">> HW_Connection: " + RESET;

    //olvasók nyilvántartása
    private static final Map<String, String> readers = new ConcurrentHashMap<>(); // key -> IP, value-> "DEV-001"

    // ÚJ: Belső állapot osztály a részletes adatok tárolására
    private static class DeviceState {
        String fullId; // "DEV-001"
        String name = "Új Olvasó";
        String ip;
        boolean isOnline = false;

        DeviceState(String fullId, String ip, boolean isOnline) {
            this.fullId = fullId;
            this.ip = ip;
            this.isOnline = isOnline;
        }
    }

    // ÚJ: Eszközök állapotának tárolása (ID -> DeviceState)
    private static final Map<String, DeviceState> devices = new ConcurrentHashMap<>();

    private static int nextReaderId = 1; // az első kiosztott ID 1 lesz, amit a configureReader fog hasznalni

    private static final int PORT = 54321; //ezen a porton hallgat a szerver
    private static final int THREAD_POOL_SIZE = 50; // egyszerre max. 50 kliens

    private static final int TOTAL_RECORD_SIZE = 19;    // egy fix bájtszám, amit a szerver olvas

    // ÚJ: Repository injektálása
    private static UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        HW_Connection.userRepository = userRepository;
    }

    public static void start_HW_Server() {

        //külön listener szál az accept() blokkoló tulajdonsága miatt --> így most megy a program a fő szálon
        // minden más mehet mellette a szervernél.
        // Ha jön egy kliens, akkor létrejön egy külön szál, ami megszűnik, ha elvégzi a feladatát.
        new Thread(() ->
        {
            ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            //^ ez egy szál kezelő szolgáltatás, létrehoz egy olyan szál poolt,
            // ami legfeljebb 50 kliens kezelését engedi

            //a szerver elindul egy porton (PORT változó) és klie4nsre vár,
            // amikor jön egy kliens, akkor elindít neki egy külön szálat (CLientHandler)
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println(PREFIX + "HW Szerver elindult a " + PORT + " porton...");

                while (true) // a szerver folyamatosan figyeli a klienseket
                {
                    Socket clientSocket = serverSocket.accept(); //ez a blokkoló hívás, vagyis addig várunk,
                    // amíg egy kliens csatlakozik
                    //^ ha jön egy új kliens, akkor létrejön egy Socket objektum, ami az adott klienssel kommunikál
                    pool.execute(new ClientHandler(clientSocket, userRepository));
                }
            } catch (IOException e) {
                System.out.println(PREFIX + RED + "IOException: " + RESET + e.getMessage());  // ha hiba van kiirjuk mi a baj
            }
        }).start();
    }

    public static int getPort()
    {
        return HW_Connection.PORT;
    }

    public static Map<String, String> getReaders() {
        return readers;
    }

    // --- ÚJ: API metódusok ---

    // Csak az online eszközöket adjuk vissza, egyszerűsített DTO formában
    public static List<ReaderDeviceDTO> getDeviceList() {
        return devices.values().stream()
                .filter(d -> d.isOnline)
                .map(d -> {
                    // "DEV-001" -> "001" levágása, hogy a frontend tegye elé
                    String shortId = d.fullId.replace("DEV-", "");
                    return new ReaderDeviceDTO(shortId, d.name, d.ip);
                })
                .collect(Collectors.toList());
    }

    // Név frissítése (ID alapján keresünk, de a frontend a rövid ID-t küldi, így vissza kell alakítani)
    public static boolean updateDeviceName(String shortId, String newName) {
        String fullId = "DEV-" + shortId;
        if (devices.containsKey(fullId)) {
            devices.get(fullId).name = newName;
            return true;
        }
        return false;
    }

    public static class ClientHandler implements Runnable  //futtatható szál legyen
    {
        private Socket socket; // ebben tároljuk az adott klienshez tartozó kapcsolatot
        private String deviceId;
        private final UserRepository ur;

        public ClientHandler(Socket socket, UserRepository ur) //konstruktor
        {
            this.socket = socket;
            this.ur = ur;
        }

        @Override
        public void run()
        {
            //lekéri az adott kliens IP címét
            String clientIP = socket.getInetAddress().getHostAddress(); //kiolvassa kliens IP címét
            System.out.println(PREFIX + "Új kliens kapcsolódott: " + clientIP); //felhasználó tájékoztatása


            //beállít két adatfolyamot
            //in: a kliens küld nekünk adatot
            //out: mi küldünk adatot a szervernek
            try (InputStream in = socket.getInputStream();
                 // bejövő adatok a klienstől
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream()))
            //kimenő adatok
            {

                byte[] buffer = new byte[TOTAL_RECORD_SIZE]; // teljes adategység 10 byte
                int bytesReadTotal = 0;

                // blokkoló olvasás a teljes 19 bájtra
                while (bytesReadTotal < TOTAL_RECORD_SIZE) {
                    int bytesRead = in.read(buffer, bytesReadTotal, TOTAL_RECORD_SIZE - bytesReadTotal);  // bináris olvasás
                    if (bytesRead == -1) {
                        System.out.println(PREFIX + "A kliens bontotta a kapcsolat: " + clientIP);
                        return; // kilépés
                    }
                    bytesReadTotal += bytesRead;
                }

                //az első 4 bájt tartalmazza az olvasó azonosítóját (little-edian)
                int readerID =  (buffer[3] & 0xFF) << 24 |
                        (buffer[2] & 0xFF) << 16 |
                        (buffer[1] & 0xFF) << 8  |
                        (buffer[0] & 0xFF); // kártyaolvasó ID-jának olvasása, little-endian szerint


                //UID feldolgozása
                //a 10 bájtos UID a 9.bájttól kezdődik
                int uidOffset = 9; // "=" utáni első bájt
                int uidLength = 10; // 10 bájtos UID
                // UID sztring előállítása stringbuilderrel
                StringBuilder uidHex = new StringBuilder();
                for (int i = 0; i < uidLength; i++) {
                    uidHex.append(String.format("%02X", buffer[uidOffset + i]));
                }


                if (readerID == Integer.MAX_VALUE) {
                    // ez az olvasó nem volt még konfigurálva, kell neki egy új ID
                    System.out.print(PREFIX + "[ID=" + readerID + ", IP=" + clientIP + "] konfigurálva -> ");
                    this.deviceId = configureReader(clientIP); //ASCII string küldése
                    out.write(this.deviceId.getBytes());    // konfigurált ID visszaküldése az olvasónak
                    System.out.println(this.deviceId);
                }
                else {
                    /**
                     * Ez az olvasó már konfigurálva van, tovább kell értelmezni az adatot, amit küldött,
                     * mert az tartalmaz egy UID-t is
                     */

                    // ÚJ: ID visszakeresése vagy újraregisztrálás
                    if (readers.containsKey(clientIP)) {
                        this.deviceId = readers.get(clientIP);
                    } else {
                        this.deviceId = String.format("DEV-%03d", readerID);
                        registerDevice(this.deviceId, clientIP);
                    }

                    System.out.println(PREFIX + "[ID=" + this.deviceId + ", IP=" + clientIP + "] üzenetének feldolgozása elkezdődött!");
                    //Pl:
                    processUID(uidHex.toString());
                }

                // ÚJ: Online státusz beállítása
                setDeviceStatus(this.deviceId, true);

            } catch (IOException e) {
                System.out.println(PREFIX + "A kliens bontotta a kapcsolatot: " + clientIP);
            }

            finally
            {
                // ÚJ: Offline státusz beállítása
                if (this.deviceId != null) {
                    setDeviceStatus(this.deviceId, false);
                }

                try
                {
                    socket.close(); //lezárja a kapcsolatot, akkor is ha hiba van (ne halmozódjanak fel a hibás kapcsolatok)
                }
                catch (IOException e)
                {
                    //ignore
                }

            }
        }

        private String configureReader(String ip)
        {
            // Ez végzi el a kértyaolvasók listázását és az ID-k kiosztását
            /**
             * A kártyaolvasókat javaslom nyilvántartani egy HashMap<Integer, String> szótárban, ahol:
             * Integer --> a konfigurált ID, egyedi kell legyen a hashmap-ben, mehet 1-től pl
             * String ---> a kártyaolvasó IP címe, mivel később szükség lehet rá, illetve a UI-ba is ki kell tenni
             *
             * A HashMap-et javaslom a HW_Conncetion osztályba tenni (külső osztály), és osztály attribútum kellene, hogy legyen.
             * Láthatósága lehet privát, különböző lekérdező metódusokat készíteni lehet hozzá.
             *
             * Ennek a metódusnak (configureReader()) a feladata, hogy ebben a HashMap-be betegye az új olvasó adatát,
             * majd a kapott sorszámot, mint ID visszaadja return-ben. A sorszám nyilvántartása szintén lehet a külső osztály privát
             * statikus attribútuma
             */

            if (readers.containsKey(ip))
            {
                String existingId = readers.get(ip);
                System.out.println(PREFIX  + ">>HW_Connection: Ismert olvasó újra csatlakozott: " + existingId + " (" + ip + ")" + RESET);
                return existingId;
            }

            String newId = String.format("DEV-%03d", nextReaderId); //3 szamjegy, balrol nullákkal kitöltve

            registerDevice(newId, ip);
            nextReaderId++;

            System.out.println(PREFIX + ">>HW_Connection: Új olvasó regisztrálva: ID=" + newId + ", IP=" + ip + RESET);

            return newId;
        }

        // ÚJ: Segédfüggvény a regisztrációhoz
        private void registerDevice(String id, String ip) {
            readers.put(ip, id);
            // Ha még nincs ilyen ID az eszközök között, létrehozzuk alapértelmezett adatokkal
            devices.putIfAbsent(id, new DeviceState(id, ip, true));
            // Ha már van (pl. csak újraindult az arduino), frissítjük az IP-t
            devices.get(id).ip = ip;
        }

        // ÚJ: Státusz kezelése
        private void setDeviceStatus(String id, boolean online) {
            if (id != null && devices.containsKey(id)) {
                devices.get(id).isOnline = online;
            }
        }

        //később bővíteni kell
        private void processUID(String uid)
        {
            // DEBUG
            System.out.println(PREFIX + "HW_Connection: Feldolgozandó üzenet: " + uid);

            // Itt van lekérdezve az ID az adatbázisból.
            if (ur != null) {
                Optional<User> result = ur.findByCardId(uid);
                // DEBUG
                if(result.isPresent()) {
                    System.out.println("User: " + result.get().getName());
                    if (result.get().getRole() == User.UserRole.TEACHER) {
                        // Ez egy tanár volt, itt kell 20p timert indítani
                    }
                    else if (result.get().getRole() == User.UserRole.STUDENT) {
                        // Ez egy hallgató, itt kell betenni az aktuális session listájába, elmenteni a megfelelő helyre.
                    }
                }
            }
        }
    }
    // hasznalat: HW_Connection.sendCommandToReader("DEV-003", HW_Command.RED_LED_ON);
    public static void sendCommandToReader(String readerId, HW_Command command)
    {
        //érték alapján keresünk kulcsot
        //keresd meg azt az IP-címet, amihez a megadott olvasó ID tartozik
        //ha nincs ilyen, akkor térj vissza null-al

        // MÓDOSÍTVA: Először a devices map-ből próbáljuk
        String targetIp = null;
        if (devices.containsKey(readerId)) {
            targetIp = devices.get(readerId).ip;
        } else {
            // Fallback a régi mapre
            targetIp = readers.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(readerId))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
        }

        if (targetIp == null)
        {
            System.out.println(PREFIX + ">>HW_Connection: Nincs ilyen olvasó ID: " + readerId);
            return;
        }

        //felépít egy új TCP kapcsolatot a megadott olvasó IP-címére, azon a porton, amin a harvder figyel
        try(Socket socket = new Socket(targetIp, PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream()))
        {

            out.writeInt(command.getCode()); // egy 4 bájtos egész számot kuld a hálozaton keresztul a kliens fele
            out.flush(); //puffer uritese --> az adatfolyamot azonnal kuld el

            System.out.println(PREFIX + ">>HW_Connection: Parancs elküldve [" + command + "] a(z) " + targetIp + " címre.");
        }
        catch (IOException e)
        {
            System.out.println(PREFIX + ">>HW_Connection: Hiba az üzenet küldésekor: " + e.getMessage());
        }
    }

    //vezérlő parqancsok
    public enum HW_Command{
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
            return code;  //lekérdezhető vele az integer érték, amit a mikrokontroller el fog várni
        }
    }
}