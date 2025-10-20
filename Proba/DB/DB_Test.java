package DB;

import java.sql.SQLException;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "Software_Code.Database.Dao")
@EntityScan(basePackages = "Software_Code.Database.Model")
public class DB_Test {
    public static void main(String[] args) throws SQLException {
        startDatabase();
        SpringApplication.run(DB_Test.class, args);
        System.out.println("http://localhost:8080/h2-console?url=jdbc:h2:mem:testdb&user=sa");
    }

    private static void startDatabase() throws SQLException {
        Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists").start();
        Server.createWebServer("-webAllowOthers", "-ifNotExists").start();
    }

}


