package HW_Connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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


    private static final Map<Integer, String> readers = new HashMap<>();
    private static int nextReaderId = 1; // az első kiosztott ID 1 lesz, amit a configureReader fog hasznalni

    private static final int PORT = 54321; //ezen a porton hallgat a szerver
    private static final int THREAD_POOL_SIZE = 50; // egyszerre max. 50 kliens

    private static final int TOTAL_RECORD_SIZE = 19;    // egy fix bájtszám, amit a szerver olvas

    public static void start_HW_Server() {
    
        //külön listener szál az accept() blokkoló tulajdonsága miatt --> így most megy a program a fő szálon
        // minden más mehet mellette a szervernél.
        // Ha jön egy kliens, akkor létrejön egy külön szál, ami megszűnik, ha elvégzi a feladatát.
        new Thread(() ->
        {
            ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            //^ ez egy szál kezelő szolgáltatás, létrehoz egy olyan szál poolt,
            // ami legfeljebb 50 kliens kezelését engedi
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println(PREFIX + "HW Szerver elindult a " + PORT + " porton...");

                while (true) // a szerver folyamatosan figyeli a klienseket
                {
                    Socket clientSocket = serverSocket.accept(); //ez a blokkoló hívás, vagyis addig várunk,
                    // amíg egy kliens csatlakozik
                    //^ ha jön egy új kliens, akkor létrejön egy Socket objektum, ami az adott klienssel kommunikál
                    pool.execute(new ClientHandler(clientSocket));
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

    public static Map<Integer, String> getReaders() {
        return readers;
    }


    public static class ClientHandler implements Runnable  //futtatható szál legyen
    {
        private Socket socket; // ebben tároljuk az adott klienshez tartozó kapcsolatot

        public ClientHandler(Socket socket) //konstruktor
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            String clientIP = socket.getInetAddress().getHostAddress(); //kiolvassa kliens IP címét
            System.out.println(PREFIX + "Új kliens kapcsolódott: " + clientIP); //felhasználó tájékoztatása

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
                int readerID =  (buffer[3] & 0xFF) << 24 |
                                (buffer[2] & 0xFF) << 16 |
                                (buffer[1] & 0xFF) << 8  |
                                (buffer[0] & 0xFF); // kártyaolvasó ID-jának olvasása, little-endian szerint

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
                    int newId = configureReader();
                    out.writeInt(newId);    // konfigurált ID visszaküldése az olvasónak
                    System.out.println(newId);
                }
                else {
                    /**
                     * Ez az olvasó már konfigurálva van, tovább kell értelmezni az adatot, amit küldött,
                     * mert az tartalmaz egy UID-t is
                     */
                    System.out.println(PREFIX + "[ID=" + readerID + ", IP=" + clientIP + "] üzenetének feldolgozása elkezdődött!");
                    //Pl:
                    processUID(uidHex.toString());
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
            int newId = nextReaderId;

            readers.put(newId, socket.getInetAddress().getHostAddress()); // beletesszuk az uj olvasot
            nextReaderId++;

            return newId;
        }

        private void processUID(String uid)
        {
            // DEBUG
            System.out.println(PREFIX + "Feldolgozandó üzenet: " + uid);
        }
    }

public static void sendCommandToReader(int readerId, HW_Command command)
{
    String ip = readers.get(readerId);

    if (ip == null)
    {
        System.out.println(">>HW_Connection: Nincs ilyen olvasó ID: " + readerId);
        return;
    }

    try(Socket socket = new Socket(ip, PORT);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream()))
    {

        out.writeInt(command.getCode()); // egy 4 bájtos egész számot kuld a hálozaton keresztul a kliens fele
        out.flush(); //puffer uritese --> az adatgófolyamot azonnal kuld el
    }
    catch (IOException e)
    {
        System.out.println(">>HW_Connection: Hiba az üzenet küldésekor: " + e.getMessage());
    }
}

    //vezérlő parqancsok
    public enum HW_Command{
        RED_LED_ON(1),
        RED_LED_OFF(2),
        GREEN_LED_ON(3),
        GREEN_LED_OFF(4),
        BLUE_LED_ON(5),
        BLUE_LED_OFF(6);

        private final int code;

        HW_Command(int code) {
            this.code = code;
        }


        public int getCode() {
            return code;  //lekérdezhető vele az integer érték, amit a mikrokontroller el fog várni
        }
    }
}


