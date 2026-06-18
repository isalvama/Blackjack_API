package cat.itacademy.blackjack.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "cat.itacademy.blackjack.game_statistics.infrastructure.jpa")
public class JpaConfig {}
