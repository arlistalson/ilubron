package ee.talson.ilubron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IlubronApplication {

	public static void main(String[] args) {
		SpringApplication.run(IlubronApplication.class, args);
	}

}
