package cat.itacademy.blackjack.demo;

import org.springframework.boot.SpringApplication;

public class TestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.from(BlackjackApplication::main).with(TestcontainersConfiguration.class).run(args);
	}
}
