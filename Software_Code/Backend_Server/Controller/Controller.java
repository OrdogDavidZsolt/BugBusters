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
import Software_Code.Backend_Server.HW_Connection.HW_Connection;
import Software_Code.Backend_Server.UI_Connection.UI_Connection;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Controller {
    
    public static void main(String[] args) {
        UI_Connection.start_UI_Server();  // UI szerver elindítása
        System.out.println(">>Controller: Starting UI Server on port " + UI_Connection.getPort());
        HW_Connection.start_HW_Server();  // HW szerver elindítása
        System.out.println(">>Controller: Starting HW Server on port " + HW_Connection.getPort());
    }
}

