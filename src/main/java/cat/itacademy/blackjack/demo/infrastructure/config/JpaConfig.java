package cat.itacademy.blackjack.demo.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository")
public class JpaConfig {}
