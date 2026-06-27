package cat.itacademy.blackjack.demo.finished_game.application.service;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfilesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortType;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.PlayerProfileSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.PlayerProfileSortType;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository.JpaPlayerProfileRepository;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.PlayerProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPlayerProfilesSortedService implements GetPlayerProfilesSortedUseCase {
    private final JpaPlayerProfileRepository jpaPlayerProfileRepository;

    @Override
    public List<PlayerProfileResponseDto> execute(PlayerProfileSortType sort) {
        return PlayerProfileResponseDto.from(jpaPlayerProfileRepository.getByCriteria(sort));
    }
}
