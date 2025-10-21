package HW_Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HW_Connection
{
    private static final int PORT = 54321; //ezen a porton hallgat a szerver
    private static final int THREAD_POOL_SIZE = 50; // egyszerre max. 50 kliens

    private static final int TOTAL_RECORD_SIZE = 10;

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
                System.out.println(">>HW_Connection: HW Szerver elindult a " + PORT + " porton...");

                while (true) // a szerver folyamatosan figyeli a klienseket
                {
                    Socket clientSocket = serverSocket.accept(); //ez a blokkoló hívás, vagyis addig várunk,
                    // amíg egy kliens csatlakozik
                    //^ ha jön egy új kliens, akkor létrejön egy Socket objektum, ami az adott klienssel kommunikál
                    pool.execute(new ClientHandler(clientSocket));
                }
            } catch (IOException e) {
                System.out.println(">>HW_Connection: IOException: " + e.getMessage());  // ha hiba van kiirjuk mi a baj
            }
        }).start();
    }

    public static int getPort()
    {
        return HW_Connection.PORT;
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
            System.out.println(">>HW_Connection: Új kliens kapcsolódott: " + clientIP); //felhasználó tájékoztatása

            // Ezt az egész beolvasós részt át kellene alakítani. Itt most string alapon megy a dolog a buffered readerrel,
            // de át kellene írni binárisra. Ezt az InputStream segítségével tudod megtenni. A létrehozott socket objektumnak
            // van egy socket.getInputStream() metódusa, ez adja vissza a bináris InputStreamet. Ezt elmented egy  változóba,
            // utána annak a változónak lesz egy .read() metódusa, ami bináris oldasásra ad lehetőséget.
            // A bemenet formátuma a következő: [ID]-UID=[uid]. Ebben az ID egy 4 byte-os int, majd 5 karakter, majd 10 byte UID
            // példásul: 12-UID=045C3CEA537680000000, csak binárisan jelenik meg. A 10 byte minden esetben ki van töltve (0-kal) 
            try (InputStream in = socket.getInputStream();
                                // bejövő adatok a klienstől

                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
                                                //kimenő adatok
            {

                byte[] buffer = new byte[TOTAL_RECORD_SIZE]; // teljes adategység 10 byte

                while (true)
                {
                    int bytesRead = in.read(buffer); //bináris olvasás
                    if (bytesRead == -1)
                    {
                        System.out.println(">>HW_Connection: A kliens bontotta a kapcsolatot: " + clientIP);
                        break;
                    }

                }

            } catch (IOException e) {
                System.out.println(">>HW_Connection: A kliens bontotta a kapcsolatot: " + clientIP);
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
    }
}


