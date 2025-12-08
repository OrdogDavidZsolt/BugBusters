package hu.bugbusters.checkinapp.backendserver.maincomponents.hwconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;

import hu.bugbusters.checkinapp.backendserver.maincomponents.services.UserService;
import hu.bugbusters.checkinapp.database.model.User;
import hu.bugbusters.checkinapp.database.repository.UserRepository;


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
    private static final Map<String, String> readers = new HashMap<>(); // key -> IP, value-> "DEV-001"
    private static final Map<String, Long> lastHeartbeats = new ConcurrentHashMap<>();   // key -> readerID, value -> utolsó jel ideje
    private static int nextReaderId = 1; // az első kiosztott ID 1 lesz, amit a configureReader fog hasznalni

    private static final int DATA_PORT      = 54321; // ezen a porton hallgat a szerver
    private static final int HEARTBEAT_PORT = 54322; // itt várjuk a harver státuszokat
    private static final int THREAD_POOL_SIZE = 50; // egyszerre max. 50 kliens

    private static final int TOTAL_RECORD_SIZE = 19;    // egy fix bájtszám, amit a szerver olvas

    public static void start_HW_Server(UserService userService) {
    
        //külön listener szál az accept() blokkoló tulajdonsága miatt --> így most megy a program a fő szálon
        // minden más mehet mellette a szervernél.
        // Ha jön egy kliens, akkor létrejön egy külön szál, ami megszűnik, ha elvégzi a feladatát.
        new Thread(() ->
        {
            ExecutorService dataPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            //^ ez egy szál kezelő szolgáltatás, létrehoz egy olyan szál poolt,
            // ami legfeljebb 50 kliens kezelését engedi

            //a szerver elindul egy porton (PORT változó) és kliensre vár,
            // amikor jön egy kliens, akkor elindít neki egy külön szálat (CLientHandler)
            try (ServerSocket serverSocket = new ServerSocket(DATA_PORT)) {
                System.out.println(PREFIX + "HW Szerver (data) elindult a " + DATA_PORT + " porton...");

                while (true) // a szerver folyamatosan figyeli a klienseket
                {
                    Socket clientSocket = serverSocket.accept(); //ez a blokkoló hívás, vagyis addig várunk,
                    // amíg egy kliens csatlakozik
                    //^ ha jön egy új kliens, akkor létrejön egy Socket objektum, ami az adott klienssel kommunikál
                    dataPool.execute(new ClientHandler(clientSocket, userService));
                }
            } catch (IOException e) {
                System.out.println(PREFIX + RED + "IOException: " + RESET + e.getMessage());  // ha hiba van kiirjuk mi a baj
            }
        }).start();

        // Egy új szál a heartbeat-es logika implementálásáras
        new Thread(() -> {
            ExecutorService heartbeatPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            try (ServerSocket serverSocket = new ServerSocket(HEARTBEAT_PORT)) {
                System.out.println(PREFIX + "HW Szerver (heartbeat) elindult a " + HEARTBEAT_PORT + " porton...");
                while (true) {
                    Socket clientSocket = serverSocket.accept();

                    heartbeatPool.execute(new HeartbeatHandler(clientSocket)); // TODO: Ide jön a heartbeat kezelő
                }
            } catch (IOException e) {
                System.out.println(PREFIX + RED + "IOException: " + RESET + e.getMessage());
            }
        }).start();
    }

    public static int getDataPort()
    {
        return HW_Connection.DATA_PORT;
    }

    public static Map<String, String> getReaders() {
        long now = System.currentTimeMillis();

        Map<String, String> onlineReaders = new HashMap<>();
        for (var entry : readers.entrySet()) {
            String ip = entry.getKey();
            String readerID = entry.getValue();

            Long last = lastHeartbeats.get(readerID);
            if (last != null && (now - last <= 30_000)) {
                onlineReaders.put(ip, readerID);
            }
        }
        System.out.println("Online readers: " + onlineReaders);
        return onlineReaders;
    }


    public static class ClientHandler implements Runnable  //futtatható szál legyen
    {
        private Socket socket; // ebben tároljuk az adott klienshez tartozó kapcsolatot


        private UserService userService;

        public ClientHandler(Socket socket, UserService userService) //konstruktor
        {
            this.socket = socket;
            this.userService = userService;
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
                    int newId = configureReader(); //ASCII string küldése
                    out.writeInt(newId);    // konfigurált ID visszaküldése az olvasónak
                    System.out.println(PREFIX + "[ID=" + readerID + ", IP=" + clientIP + "] konfigurálva -> ID=" + newId);
                }
                else {
                    /**
                     * Ez az olvasó már konfigurálva van, tovább kell értelmezni az adatot, amit küldött,
                     * mert az tartalmaz egy UID-t is
                     */
                    System.out.println(PREFIX + "[ID=" + readerID + ", IP=" + clientIP + "] üzenetének feldolgozása elkezdődött!");
                    //Pl:
                    // Ez az eredmény befolyásolja a kontroll LED-eket a hardveren
                    boolean result = processUID(uidHex.toString());
                    // Itt ki kellene írni a választ a socketen keresztül.
                }

            } catch (IOException e) {
                System.out.println(PREFIX + "A kliens bontotta a kapcsolatot: " + clientIP);
            }

            finally
            {
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

        private int configureReader()
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
            String ip = socket.getInetAddress().getHostAddress();

            if (readers.containsKey(ip))
            {
                String existingId = readers.get(ip);
                System.out.println(PREFIX  + ">>HW_Connection: Ismert olvasó újra csatlakozott: " + existingId + " (" + ip + ")" + RESET);

                String lastThree = existingId.substring(existingId.length() - 3);
                int readerIdInt = Integer.parseInt(lastThree);
                return readerIdInt;
            }

            int newID = nextReaderId;
            String newIdStr = String.format("DEV-%03d", nextReaderId); //3 szamjegy, balrol nullákkal kitöltve
            readers.put(ip, newIdStr);
            nextReaderId++;

            System.out.println(PREFIX + "Új olvasó regisztrálva: ID=" + newID + ", IP=" + ip + " (" + newIdStr + ")" + RESET);

            return newID;
        }

        //később bővíteni kell
        private boolean processUID(String uid)
        {
            // DEBUG
            System.out.println(PREFIX + "HW_Connection: Feldolgozandó üzenet: " + uid);

            // Itt van lekérdezve az ID az adatbázisból. 
            Optional<User> result = userService.findByCardId(uid);
            if (result.isEmpty()) {
                System.out.println(PREFIX + "Nincs eredmény a kártyához");
                return false; // Piros LED
            }
            // DEBUG
            System.out.println(result);
            if (result.get().getRole() == User.UserRole.TEACHER) {
                // Ez egy tanár volt, itt kell 20p timert indítani
                System.out.println(PREFIX + "Ez egy tanári ID");
            }
            else if (result.get().getRole() == User.UserRole.STUDENT) {
                // Ez egy hallgató, itt kell betenni az aktuális session listájába, elmenteni a megfelelő helyre.
                System.out.println(PREFIX + "Ez egy diák kártya volt");
            }
            return true; // Zöld LED
        }
    }
   
    public static class HeartbeatHandler implements Runnable
    {
        private Socket socket;

        public HeartbeatHandler(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (InputStream in = socket.getInputStream()) {
                byte[] buffer = new byte[4];
                int read = in.read(buffer);
                if (read == 4) {
                    int readerId = ((buffer[0] & 0xFF) << 24) |
                                ((buffer[1] & 0xFF) << 16) |
                                ((buffer[2] & 0xFF) << 8)  |
                                (buffer[3] & 0xFF);
                    //System.out.println(PREFIX + "Heartbeat ID: " + readerId);
                    // itt frissíted a statikus map-et:
                    String readerIdStr = String.format("DEV-%03d", readerId);
                    lastHeartbeats.put(readerIdStr, System.currentTimeMillis());
                    //System.out.println(PREFIX + "Heartbeat: " + readerId);
                }
            } catch (IOException e) {
                System.out.println(PREFIX + "Heartbeat handler IO exception: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
        
    }

    // hasznalat: HW_Connection.sendCommandToReader("DEV-003", HW_Command.RED_LED_ON);
    public static void sendCommandToReader(String readerId, HW_Command command)
    {
        //érték alapján keresünk kulcsot
        //keresd meg azt az IP-címet, amihez a megadott olvasó ID tartozik
        //ha nincs ilyen, akkor térj vissza null-al
        String targetIp = readers.entrySet().stream()
                .filter(entry -> entry.getValue().equals(readerId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (targetIp == null)
        {
            System.out.println(PREFIX + ">>HW_Connection: Nincs ilyen olvasó ID: " + readerId);
            return;
        }

        //felépít egy új TCP kapcsolatot a megadott olvasó IP-címére, azon a porton, amin a harvder figyel
        try(Socket socket = new Socket(targetIp, DATA_PORT);
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


