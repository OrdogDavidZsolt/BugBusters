package HW_Connection;

public class szalkezelesProba
{
    public static void main(String[] args)
    {
        Runnable t = () -> {
           String s = Thread.currentThread().getName();
            System.out.println("Fut a szál: " + s);
        };

        //inditunk ket kulon szalat
        new Thread(t, "Elso szal").start();
        new Thread(t, "Masodik szal").start();

        System.out.println("Fut a main szal");
    }
}
