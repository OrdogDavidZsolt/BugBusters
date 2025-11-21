package Controller;

// ------------- Importok -------------
// Saját komponensek
import HW_Connection.HW_Connection;
import UI_Connection.UI_Connection;
import DB_Connection.DB_Connection;

// Spring-boot komponensek
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"Controller", "Repository", "Model", "DB_Connection"})
@EnableJpaRepositories(basePackages = "Repository")
@EntityScan(basePackages = "Model")
public class Controller {

    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE   = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN   = "\u001B[36m";
    private static final String WHITE  = "\u001B[37m";
    private static final String PREFIX = YELLOW + ">> Controller: " + RESET;


    public static void main(String[] args) {
        System.out.println("=== Starting application ===");
        // Spring ApplicationContext elindítása
        var context = SpringApplication.run(Controller.class, args);

        // Spring-ből lekérjük a DB_Connection példányt
        DB_Connection dbConnection = context.getBean(DB_Connection.class);

        System.out.println(PREFIX + "Starting h2 Database");
        dbConnection.startDatabase();     // DB indítása a spring segítségével 
        
        // Saját szerverek indítása
        System.out.println(PREFIX + "Starting UI Server on port " + UI_Connection.getPort());
        UI_Connection.start_UI_Server();  // UI szerver elindítása
        
        System.out.println(PREFIX + "Starting HW Server on port " + HW_Connection.getPort());
        HW_Connection.start_HW_Server();  // HW szerver elindítása
        
    }
}

