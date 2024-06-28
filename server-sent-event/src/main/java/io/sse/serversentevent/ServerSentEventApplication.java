package io.sse.serversentevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ServerSentEventApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerSentEventApplication.class, args);
    }

}
