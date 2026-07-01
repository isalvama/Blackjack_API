package cat.itacademy.blackjack.demo.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "cat.itacademy.blackjack.demo.active_game.infrastructure.persistence.mongodb.springdatarepository")
public class MongoConfig {}
