package Software_Code.Backend_Server.HW_Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable  //futtatható szál legyen
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
