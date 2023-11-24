package account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccountServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceApplication.class);

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Application was launched!");
    }
}