package CommunicationExample;

// Ez a Sender osztály küld adatot, de csak az interfészt ismeri
public class Sender {
    private DataReceiver receiver;

    public Sender(DataReceiver receiver) {
        this.receiver = receiver;
    }

    public void sendData(String data) {
        receiver.receiveData(data);
    }
}
