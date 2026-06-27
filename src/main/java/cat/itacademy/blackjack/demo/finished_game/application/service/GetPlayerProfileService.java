package cat.itacademy.blackjack.demo.finished_game.application.service;

import cat.itacademy.blackjack.demo.finished_game.application.exception.PlayerProfileNotFound;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfileUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.model.PlayerProfile;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository.JpaPlayerProfileRepository;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.PlayerProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetPlayerProfileService implements GetPlayerProfileUseCase {
    private final JpaPlayerProfileRepository jpaPlayerProfileRepository;

    @Override
    public PlayerProfileResponseDto getPlayerById(Long id) {
        Optional<PlayerProfile> entity = jpaPlayerProfileRepository.findById(id);
        return entity.map(PlayerProfileResponseDto::from).orElseThrow(() -> new PlayerProfileNotFound("No players found with id " + id));
    }

    @Override
    public PlayerProfileResponseDto getPlayerByName(String name) {
        Optional<PlayerProfile> entity = jpaPlayerProfileRepository.findByName(name);
        return entity.map(PlayerProfileResponseDto::from).orElseThrow(() -> new PlayerProfileNotFound("No players found with name " + name));
    }
}
