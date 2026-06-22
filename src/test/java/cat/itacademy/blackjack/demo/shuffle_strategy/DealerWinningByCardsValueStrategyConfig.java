package cat.itacademy.blackjack.demo.shuffle_strategy;

import cat.itacademy.blackjack.demo.active_game.application.service.shuffle_strategy.ShuffleStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class DealerWinningByCardsValueStrategyConfig {

    @Bean
    @Primary
    public ShuffleStrategy dealerWinningByCardsValueStrategy(){
        return new DealerWinningByCardsValueStrategy();
    }
}
