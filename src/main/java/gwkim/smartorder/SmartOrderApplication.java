package gwkim.smartorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmartOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartOrderApplication.class, args);
	}

}
