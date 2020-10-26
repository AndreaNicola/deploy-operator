package it.makeit.operator;

import it.makeit.operator.crd.app.AppWatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * this class serves as an entry point for the Spring Boot app
 * Here, we check to ensure all required environment variables are set
 */
@SpringBootApplication
@Slf4j
public class OperatorApplication {

    public static void main(final String[] args) throws Exception {
        String value = System.getenv("PORT");

        if (value == null) {
            log.debug("error: PORT environment variable not set");
            System.exit(1);
        }

        SpringApplication.run(OperatorApplication.class, args);
        AppWatcher.init();

    }
}
