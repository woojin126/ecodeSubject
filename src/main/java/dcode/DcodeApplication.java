package dcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DcodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DcodeApplication.class, args);
    }

}
