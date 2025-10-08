package Software_Code.Backend_Server.HW_Connection;

import java.io.BufferedReader;
import java.io.IOException;
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

    public static void start_HW_Server() {

    
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        //^ ez egy szál kezelő szolgáltatás, létrehoz egy olyan szál poolt, ami legfeljebb 50 klien kezelését engedi

        try(ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println(">>HW_Connection: HW Szerver elindult a " + PORT + " porton...");

            while (true) // a szerver folyamatosan figyeli a klienseket
            {
                Socket clientSocket = serverSocket.accept(); //ez a blokkoló hívás, vagyis addig várunk, amíg egy kliens csatlakozik
                //^ ha jön egy új kliens, akkor létrejön egy Socket objektum, ami az adott klienssel kommunikál

                pool.execute(new ClientHandler(clientSocket));
            }
        }
        catch (IOException e)
        {
            System.out.println(">>HW_Connection: IOException: " + e.getMessage());  // ha hiba van kiirjuk mi a baj
        }
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

            try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                // ^  karakteresen olvassuk               ^ soronként tudjuk olvasni     ^ ezek a bejövő adatok a klienstől

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // ^ kimenő adatok a kliensnek   ^ azonnal elküldi a kliensnek
                String line;

                while((line = in.readLine()) != null) //beolvassa soronként, amit a kliens küld
                {
                    System.out.println(">>HW_Conncetion: [" + clientIP + "] -> " + line); //mit küldött a kliens

                    // válasz a kliensnek
                    out.println("OK, megkaptam: " + line); // Ez nem tudom mennyire lesz jó, mert a kliens nem fogja tudni kiírni ezt sehová sem
                }

            }
            catch (IOException e)
            {
                System.out.println(">>HW_Conncetion: A kliens bontotta a kapcsolatot: " + clientIP);
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


