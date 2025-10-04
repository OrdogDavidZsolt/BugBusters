/**
 * Ebben az osztályban egy web kiszolgálót implementálok, mely a UI kapcsolatért felel
 * 
 * Az osztályban egy 80-as porton működő HTTP szerver van, amely adatokat küld és fogad,
 * illetve a HTML oldalak betöltéséért felel
 */

package Software_Code.Backend_Server.UI_Connection;

// ------------- Importok -------------
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;


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
}
