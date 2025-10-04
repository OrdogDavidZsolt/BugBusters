/**
 * Ez a fájl indítja el a teljes backend szervert
 * A többi kódban csak metódusok (API-k) legyenek, main nem kell!
 * 
 * Fordítás:
 * ~/Szoftverfejlesztes$ javac ./Software_Code/Backend_Server/Controller/Controller.java
 * Futtatás:
 * ~/Szoftverfejlesztes$ sudo java Software_Code.Backend_Server.Controller.Controller 
 * 
 * A sudo azért kell, mert a tűzfalon átállítja a kód a 80-as port elérhetőségét
 */ 

package Software_Code.Backend_Server.Controller;


// ------------- Importok -------------
// UI_Conncetion szükségletek
import com.sun.net.httpserver.HttpServer;
import Software_Code.Backend_Server.UI_Connection.UI_Connection;

import java.io.IOException;
import java.net.InetSocketAddress;



public class Controller {
    
    public static void main(String[] args) {
        start_UI_Server();  // UI szerver elindítása
    }


    public static void start_UI_Server()
    {
        try
        {
            /**
             * Létrehozzuk a HTTP szerver objektumot
             * InetSocketAddress: Megmondja, hogy a futtató gép ip címén létezzen a szerver, illetve, hogy melyik porton
             * Második paraméter: backlog -> a várakozó kapcsolatok száma
             *      Mivel backlog == 0, a váarkozó kapcsolatok számát az oprendszerfogja korlátozni max. 128-ra (!)
             * Ez a  .create() metódus még nem indítja el a szervert, csak a kódobjektumot hozza létre
             */
            HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
            /**
             * createContext: endpoint hozzáadása a szerverhez
             *      második paraméter a HttpHandler-t implementáló osztály: UI_Connection.java
             *      ez az osztály tartalmaz egy 'public void handle(HttpExchange exchange)' metódust
             */
            server.createContext("/", new UI_Connection());
            /**
             * setExecutor: a HttpServer külön szálakon képes kéréseket kezelni
             * az executor határozza meg a szálakat
             * null -> egy beépített automatikus szálkezelő logika, nem kell 'bántani'
             * máshogy: nem konfigurálok saját thread poolt a szerverhez, azautomaikusat használom
             */
            server.setExecutor(null);
            /**
             * a server.start() indítja el valójában a szervert és megnyitja a 80-as portot
             * innentől a szerver figyeli a 80-as portra érkező HTTP kéréseket
             * minden kéréshez meghívja a megfelelő handlert
             */
            server.start();

            System.out.println(">>Controller: UI_Server started on port 80!");
        }
        catch (IOException e)
        {
            System.out.println(">>Controller: IO exception upon UI_Server start: " + e.getMessage());
        }
    }
}

