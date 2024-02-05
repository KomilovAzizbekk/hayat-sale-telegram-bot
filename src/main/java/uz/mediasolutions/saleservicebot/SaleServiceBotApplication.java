package uz.mediasolutions.saleservicebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaleServiceBotApplication {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        SpringApplication.run(SaleServiceBotApplication.class, args);
    }

}
