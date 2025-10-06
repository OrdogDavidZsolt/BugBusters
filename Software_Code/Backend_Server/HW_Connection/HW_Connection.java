package Software_Code.Backend_Server.HW_Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HW_Connection
{
    private static final int PORT = 54321; //ezen a porton hallgat a szerver
    private static final int THREAD_POOL_SIZE = 50; // egyszerre max. 50 kliens

    public static void main(String[] args) {


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
}
