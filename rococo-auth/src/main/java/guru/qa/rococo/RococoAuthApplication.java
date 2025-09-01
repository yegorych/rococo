package guru.qa.rococo;

import guru.qa.rococo.service.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoAuthApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RococoAuthApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }
}
