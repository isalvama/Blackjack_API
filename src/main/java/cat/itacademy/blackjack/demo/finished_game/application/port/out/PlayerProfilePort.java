package cat.itacademy.blackjack.demo.finished_game.application.port.out;

import cat.itacademy.blackjack.demo.finished_game.domain.criteria.PlayerProfileSortType;
import cat.itacademy.blackjack.demo.finished_game.domain.model.PlayerProfile;

import java.util.List;
import java.util.Optional;

public interface PlayerProfilePort {
    Optional<PlayerProfile> findByName(String name);

    PlayerProfile save(PlayerProfile playerProfile);

    List<PlayerProfile> findAllSorted(PlayerProfileSortType sort);

    Optional<PlayerProfile> findById(Long id);
}

