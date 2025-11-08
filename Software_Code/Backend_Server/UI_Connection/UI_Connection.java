/**
 * Ebben az osztályban egy web kiszolgálót implementálok, mely a UI kapcsolatért felel
 * 
 * Az osztályban egy 80-as porton működő HTTP szerver van, amely adatokat küld és fogad,
 * illetve a HTML oldalak betöltéséért felel
 */

package UI_Connection;

// ------------- Importok -------------
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class UI_Connection
{

    private static final int PORT = 80; // tipikus HTTP port
    
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
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            /**
             * createContext: endpoint hozzáadása a szerverhez
             *      második paraméter a HttpHandler-t implementáló osztály: UI_Connection.java
             *      ez az osztály tartalmaz egy 'public void handle(HttpExchange exchange)' metódust
             */
            server.createContext("/", new StaticFileHandler("Software_Code/UI"));
            server.createContext("/data", new LoginDataHandler());
            server.createContext("/admin", new AdminDataHandler());
            server.createContext("/student-data", new StudentDataHandler());
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

            System.out.println(">> UI_Connection: UI_Server started on port " + PORT + "!");
        }
        catch (IOException e)
        {
            System.out.println(">> UI_Connection: IO exception upon UI_Server start: " + e.getMessage());
        }
    }

    public static int getPort()
    {
        return UI_Connection.PORT;
    }

    /* ----------- Handler Osztályok ----------- */
    /**
     * Tartalomhoz használt handler(ek):
     *      StaticFileHandler (tartalom kiszolgáló)
     * 
     * Kommunikációhoz használt handler(ek):
     *      LoginDataHandler (fetch POST kiszolgáló)
     *      AdminDataHandler (fetch POST kiszolgáló)
     *      StudentDataHandler (fetch POST kiszolgáló)
     * 
     * Nem használt dummy/példa handler(ek):
     *      FileHandler (tartalom kiszolgáló)
     *      DataHandler (fetch POST kiszolgáló)
     */
    static public class FileHandler implements HttpHandler {
        private final String filePath;
        private final String contentType;

        public FileHandler(String filePath, String contentType)
        {
            this.filePath = filePath;
            this.contentType = contentType;
        }

        @Override
        public void handle(HttpExchange exchange) {
            /**
             * Ez a metódus fut le minden egyes alkalommal, amikor egy kliens, pl böngésző kérést küld a szerverre
             * Ez a metódus fog választ adni a kérésre, egy magadott file-ból
             */
            System.out.println(">> UI_Connection: New UI req.:" + filePath);
            try
            {
                File file = new File(filePath);
                if (!file.exists()) {
                    String notFound = "404 Not Found";
                    exchange.sendResponseHeaders(404, notFound.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(notFound.getBytes());
                    }
                    return;
                }

                byte[] bytes = Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);

                try (OutputStream os = exchange.getResponseBody())
                {
                    os.write(bytes);
                }
            }
            catch (IOException e)
            {
                System.out.println(">> UI_Connection: IO Exception when handling response: " + e.getMessage());
            }
            System.out.println(">> UI_Connection: Done:" + filePath);
        }
        
    }

    static class StaticFileHandler implements HttpHandler {
        /**
         * Ez a kiszolgáló egy statikus tartalomkiszolgáló
         *  A webfelület gyökérmappája megadásra kerül a konstruktorban,
         *  innentől kezdve a kiszolgáló tudja, hogy a kérésre melyik fájlt kell elküldje
         */

        private final Path rootDir;
        private final Map<String, String> mimeTypes = new HashMap<>();

        public StaticFileHandler(String rootDir) {
            this.rootDir = Paths.get(rootDir);

            // Tartalomtípusok (Content-Type)
            mimeTypes.put("html", "text/html");
            mimeTypes.put("css", "text/css");
            mimeTypes.put("js", "application/javascript");
            mimeTypes.put("ico", "image/x-icon");
            mimeTypes.put("png", "image/png");
            mimeTypes.put("jpg", "image/jpeg");
            mimeTypes.put("jpeg", "image/jpeg");
            mimeTypes.put("gif", "image/gif");
            mimeTypes.put("svg", "image/svg+xml");
        }

        @Override
        public void handle(HttpExchange exchange) {
            try (OutputStream os = exchange.getResponseBody()) {
                String requestedPath = exchange.getRequestURI().getPath();
                System.out.println(">> UI_Connection: Request: " + requestedPath);
                // Ha csak "/"-t kér, legyen index.html
                if (requestedPath.equals("/")) {
                    requestedPath = "/index.html";
                }

                Path filePath = rootDir.resolve("." + requestedPath).normalize();
                if (!Files.exists(filePath) || !filePath.startsWith(rootDir)) {
                    String notFound = "404 Not Found";
                    exchange.sendResponseHeaders(404, notFound.length());
                    os.write(notFound.getBytes());
                    return;
                }

                // Kiterjesztés alapján MIME-típus
                String ext = "";
                int dot = filePath.toString().lastIndexOf('.');
                if (dot >= 0) ext = filePath.toString().substring(dot + 1).toLowerCase();
                String mimeType = mimeTypes.getOrDefault(ext, "application/octet-stream");

                // Válasz küldése
                byte[] bytes = Files.readAllBytes(filePath);
                exchange.getResponseHeaders().set("Content-Type", mimeType + "; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);

                os.write(bytes);
            }
            catch (IOException e)
            {
                System.out.println(">> UI_Connection: IO Exception raised when handling content request: " + e.getMessage());
            }
        }
    }

    static public class DataHandler implements HttpHandler {
        private String response;
        private int responseCode;

        @Override
        public void handle(HttpExchange exchange)
        {
            try (OutputStream os = exchange.getResponseBody()) {
                // Csak POST kérésekre fókuszál
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod()))
                {
                    // Body kiolvasása a kérelemből
                    byte[] data = exchange.getRequestBody().readAllBytes();
                    String body = new String(data, "UTF-8");

                    // Debug infó
                    System.out.println(">> UI_Connection: Received data: " + body);

                    // Kötelező HTTP válasz - 200-as kóddal
                    this.response = "Data received";
                    this.responseCode = 200;
                }
                else // Nem POST kérések
                {
                    this.response = "Method Not Allowed";
                    this.responseCode = 405;
                }
                
                exchange.sendResponseHeaders(this.responseCode, this.response.length());
            
                os.write(this.response.getBytes());
            }
            catch (IOException e)
            {
                System.out.println(">> UI_Connection: IO Exception raised: " + e.getMessage());
            }
        }
    }

    static public class LoginDataHandler implements HttpHandler {
        /**
         * Ez a kiszolgáló kezeli a bejelentkezési adatokat
         *  Lekérdezi helyességüket az adatbázisból, majd visszaküld egy választ az elfogadás értelmében
         */
        private String response;
        private int responseCode;

        @Override
        public void handle(HttpExchange exchange)
        {
            try (OutputStream os = exchange.getResponseBody()) {
                // Csak POST kérésekre fókuszál
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod()))
                {
                    // Body kiolvasása a kérelemből
                    byte[] data = exchange.getRequestBody().readAllBytes();
                    String body = new String(data, "UTF-8");

                    // Debug infó
                    System.out.println(">> UI_Connection: Received data: " + body);


                    // Egyszerű JSON parse - ha nincs külső lib, akkor manuálisan:
                    boolean success = false;
                    String message = "";

                    /* példa, majd adatbázisból jön */
                    if (body.contains("\"username\":\"admin\"") && body.contains("\"password\":\"admin\"") && body.contains("\"mode\":\"admin\"")) {
                        success = true;
                        message = "Login successful!";
                    } else if (body.contains("\"username\":\"teacher1\"") && body.contains("\"password\":\"1234\"") && body.contains("\"mode\":\"teacher\"")) {
                        success = true;
                        message = "Login successful!";
                    } else {
                        success = false;
                        message = "Invalid username, password or login mode (teacher / admin)!";
                    }
                    /* Idáig kell az adatbázisból lekérdezni */

                    String jsonResponse = String.format(
                        "{\"success\": %b, \"message\": \"%s\"}",
                        success, message
                    );

                    // Kötelező HTTP válasz - 200-as kóddal
                    // a válasz majd az adatbázisból fog visszajönni
                    this.response = jsonResponse;
                    this.responseCode = 200;
                }
                else // Nem POST kérések
                {
                    this.response = "Method Not Allowed";
                    this.responseCode = 405;
                }
                
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(this.responseCode, this.response.length());
            
                os.write(this.response.getBytes());
            }
            catch (IOException e)
            {
                System.out.println(">> UI_Connection: IO Exception raised: " + e.getMessage());
            }
        }
        
    }

    static public class AdminDataHandler implements HttpHandler {
        /**
         * Ez a kiszolgáló kezeli az admin felületről beérkező kéréseket
         *  Feladatok közé tartozik:
         *      Betöltéskor a csatlakoztatott kártyaolvasók listájának elküldése
         *      Frissítés esetén a csatlakoztatott kártyaolvasók listájának elküldése
         *      
         */
        private String response;
        private int responseCode;


        @Override
        public void handle(HttpExchange exchange) {
            try (OutputStream os = exchange.getResponseBody()) {
                // Csak POST kérésekre fókuszál
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod()))
                {
                    // Body kiolvasása a kérelemből
                    byte[] data = exchange.getRequestBody().readAllBytes();
                    String body = new String(data, "UTF-8");

                    // Debug infó
                    System.out.println(">> UI_Connection: Received data: " + body);

                    // Kötelező HTTP válasz - 200-as kóddal
                    this.response = "Data received";
                    this.responseCode = 200;
                }
                else // Nem POST kérések
                {
                    this.response = "Method Not Allowed";
                    this.responseCode = 405;
                }
                
                exchange.sendResponseHeaders(this.responseCode, this.response.length());
            
                os.write(this.response.getBytes());
            }
            catch (IOException e)
            {
                System.out.println(">> UI_Connection: IO Exception raised: " + e.getMessage());
            }
        }

    }

    static public class StudentDataHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'handle'");
        }

    }
}
