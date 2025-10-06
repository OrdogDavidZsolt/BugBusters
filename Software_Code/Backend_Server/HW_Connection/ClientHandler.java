package Software_Code.Backend_Server.HW_Connection;

import java.net.Socket;

public class ClientHandler implements Runnable //futtatható szál legyen
{
    private Socket socket; // ebben tároljuk az adott klienshez tartozó kapcsolatot


    public ClientHandler(Socket socket) //konstruktor
    {
        this.socket = socket;
    }
}
