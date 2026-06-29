package cat.itacademy.blackjack.demo.shuffle_strategy;

import cat.itacademy.blackjack.demo.active_game.domain.shuffle_strategy.ShuffleStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TieWithoutBlackJackStrategyConfig {
    @Bean
    @Primary
    public ShuffleStrategy tieWithoutBlackJackStrategy(){
        return new TieWithoutBlackJackStrategy();
    }
}
