package ServerCode;

import java.io.*; //hálózatról való olvasáshoz kell
import java.net.*; //hálózati kapcsolatokat kezel
import java.util.concurrent.*; //a több szálon futáshoz

public class Server
{
    private static final int PORT = 54321;
    private static final int THREAD_POOL_SIZE = 50; // egyszerre max. 50 kliens

    public static void main(String[] args)
    {
        System.out.println("Szerver elindult a " + PORT + " porton...");


    }
}