package Controller;

// ------------- Importok -------------
// UI_Conncetion szükségletek
import HW_Connection.HW_Connection;
import UI_Connection.UI_Connection;
import DB_Connection.DB_Connection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Controller {
    
    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
        UI_Connection.start_UI_Server();  // UI szerver elindítása
        System.out.println(">>Controller: Starting UI Server on port " + UI_Connection.getPort());
        HW_Connection.start_HW_Server();  // HW szerver elindítása
        System.out.println(">>Controller: Starting HW Server on port " + HW_Connection.getPort());
        try {
            DB_Connection.startDatabase();
        } catch (Exception e) {
            System.out.println(">>SQL Exception");
        }
    }
}

