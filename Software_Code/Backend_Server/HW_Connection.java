package Software_Code.Backend_Server;

public class HW_Connection
{
    private static final int PORT = 54321;
    private static final int THREAD_POOL_SIZE = 50; // egyszerre max. 50 kliens

    public static void main(String[] args)
    {
        System.out.println("Szerver elindult a " + PORT + " porton...");
    }
}
