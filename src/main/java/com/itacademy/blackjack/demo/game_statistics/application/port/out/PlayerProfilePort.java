package cat.itacademy.blackjack.game_statistics.application.port.out;

import cat.itacademy.blackjack.game_statistics.domain.model.PlayerProfile;

import java.util.Optional;

public interface PlayerProfilePort {
    Optional<PlayerProfile> findByName (String name);
    PlayerProfile save (PlayerProfile playerProfile);
}

