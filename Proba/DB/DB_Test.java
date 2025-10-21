package DB;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "DB",
        "Software_Code.Database"
})
@EnableJpaRepositories(basePackages = "Software_Code.Database.Dao")
@EntityScan(basePackages = "Software_Code.Database.Model")
public class DB_Test {

    public static void main(String[] args) {
        try {
            startDatabase();
            SpringApplication.run(DB_Test.class, args);
            System.out.println("Application started successfully!");
            System.out.println("H2 Console: http://localhost:8080/h2-console?url=jdbc:h2:mem:testdb&user=sa");
        } catch (Exception e) {
            System.err.println("Failed to start the H2 server or Spring Boot application.");
        }
    }

    private static void startDatabase() {
        try {
            Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists").start();
            Server.createWebServer("-webAllowOthers", "-ifNotExists").start();
            System.out.println("H2 TCP and Web servers started successfully.");
        } catch (Exception e) {
            System.err.println("Could not start H2 server: " + e.getMessage());
        }
    }
}
