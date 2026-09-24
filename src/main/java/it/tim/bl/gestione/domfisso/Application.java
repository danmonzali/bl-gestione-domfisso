package it.tim.bl.gestione.domfisso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = { "it.tim.gup.common","it.tim.bl.gestione.domfisso.*","tim.who.is" })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

}
