package dk.ek.kinoxp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("dk.ek.kinoxp.domain")
@EnableJpaRepositories("dk.ek.kinoxp.repository")
public class KinoXpApplication {

    public static void main(String[] args) {
        SpringApplication.run(KinoXpApplication.class, args);
    }

}
