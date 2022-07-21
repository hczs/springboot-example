package icu.sunnyc.jpah2example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author Administrator
 */
@EnableJpaAuditing
@SpringBootApplication
public class JpaH2ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaH2ExampleApplication.class, args);
    }

}
