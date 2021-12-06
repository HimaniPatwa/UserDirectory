package mainapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.CISCOproject.repository")
@EntityScan("com.CISCOproject.model")
@ComponentScan("com.CISCOproject.controller")
@ComponentScan("com.CISCOproject.services")
@SpringBootApplication
public class UserDirectoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserDirectoryApplication.class, args);
	}

}
