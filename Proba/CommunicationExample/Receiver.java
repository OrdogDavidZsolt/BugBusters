package CommunicationExample;

// Ez a Receiver osztály implementálja az interfészt
public class Receiver implements DataReceiver {
    private String receivedData;

    @Override
    public void receiveData(String data) {
        this.receivedData = data;
        System.out.println("Receiver received: " + receivedData);
    }
}

