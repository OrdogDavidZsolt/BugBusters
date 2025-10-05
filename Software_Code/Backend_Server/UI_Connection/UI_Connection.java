/**
 * Ebben az osztályban egy web kiszolgálót implementálok, mely a UI kapcsolatért felel
 * 
 * Az osztályban egy 80-as porton működő HTTP szerver van, amely adatokat küld és fogad,
 * illetve a HTML oldalak betöltéséért felel
 */

package Software_Code.Backend_Server.UI_Connection;

// ------------- Importok -------------
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;


public class UI_Connection implements HttpHandler
{
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        /**
         * Ez a metódus fut le minden egyes alkalommal, amikor egy kliens, pl böngésző kérést küld a szerverre
         * Ez a metódus fog választ adni a kérésre
         */
        String response = "<h1>Hello from Java server! Second test message</h1>"; // példa resonpse

        try
        {
            // Válasz fejléce a http kérésre, 200-as kód és a teljes válasz (body) mérete  
            exchange.sendResponseHeaders(200, response.getBytes().length);
            
            // Teljes válasz (body) küldése a kérésre
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());  // tényleges küldés
            os.close(); // a szerver végzett a válasszal, lezérja az output streamet
        }
        catch (IOException e)
        {
            System.out.println(">>UI_Connection: IO Exception when handling response: " + e.getMessage());
        }
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
