package Controller;

// ------------- Importok -------------
// UI_Conncetion szükségletek
import HW_Connection.HW_Connection;
import UI_Connection.UI_Connection;
import DB_Connection.DB_Connection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "Controller",
    "DB_Connection",
    "Dao",
    "Manager",
    "JPAEntityDAO",
    "Model"
})
@EntityScan(basePackages = {"Model"})
public class Controller {
    
    public static void main(String[] args) {
        
        // Spring ApplicationContext elindítása
        var context = SpringApplication.run(Controller.class, args);

        // Spring-ből lekérjük a DB_Connection példányt
        DB_Connection dbConnection = context.getBean(DB_Connection.class);

        try {
            dbConnection.startDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(">>SQL Exception");
        }

        // Saját szerverek indítása
        UI_Connection.start_UI_Server();  // UI szerver elindítása
        System.out.println(">>Controller: Starting UI Server on port " + UI_Connection.getPort());
        HW_Connection.start_HW_Server();  // HW szerver elindítása
        System.out.println(">>Controller: Starting HW Server on port " + HW_Connection.getPort());
    }
}

