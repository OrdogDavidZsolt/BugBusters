package CommunicationExample;

// Futtató osztály
public class CommunicationExample {
    public static void main(String[] args) {
        Receiver receiver = new Receiver();
        Sender sender = new Sender(receiver);
        sender.sendData("Hello World!");
    }
}
