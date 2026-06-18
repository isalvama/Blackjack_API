package cat.itacademy.blackjack.demo.game_statistics.application.port.out;

import cat.itacademy.blackjack.demo.game_statistics.domain.model.PlayerProfile;

import java.util.Optional;

public interface PlayerProfilePort {
    Optional<PlayerProfile> findByName (String name);
    PlayerProfile save (PlayerProfile playerProfile);
}

