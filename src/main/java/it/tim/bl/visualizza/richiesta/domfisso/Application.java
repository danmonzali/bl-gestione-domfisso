package it.tim.bl.visualizza.richiesta.domfisso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.ws.config.annotation.EnableWs;

@EnableAsync
@EnableWs
@SpringBootApplication(scanBasePackages = { "it.tim.gup.common","it.tim.bl.visualizza.richiesta.domfisso.*","tim.who.is" })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

}
