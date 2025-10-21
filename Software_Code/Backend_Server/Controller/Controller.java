package Controller;

// ------------- Importok -------------
// UI_Conncetion szükségletek
import HW_Connection.HW_Connection;
import UI_Connection.UI_Connection;

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

