package cat.itacademy.blackjack.demo.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "cat.itacademy.blackjack.game.infrastructure.persistence.mongodb")
public class MongoConfig {}
