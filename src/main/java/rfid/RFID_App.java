package rfid; // Fő csomag

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication // ENNYI! Nem kell semmi más paraméter!
    public class RFID_App {

        private static final String RESET  = "\u001B[0m";
        private static final String RED    = "\u001B[31m";
        private static final String GREEN  = "\u001B[32m";
        private static final String YELLOW = "\u001B[33m";
        private static final String BLUE   = "\u001B[34m";
        private static final String PURPLE = "\u001B[35m";
        private static final String CYAN   = "\u001B[36m";
        private static final String WHITE  = "\u001B[37m";
        private static final String PREFIX = YELLOW + ">> RFID_App: " + RESET;

        public static void main(String[] args) {
            // Ha van valami HW indítás, az maradhat
            // rfid.HW_Connection.start();
            SpringApplication.run(rfid.RFID_App.class, args);
        }
    }

