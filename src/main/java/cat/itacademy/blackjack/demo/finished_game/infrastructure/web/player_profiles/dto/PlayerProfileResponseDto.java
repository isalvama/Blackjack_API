package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.player_profiles.dto;

import cat.itacademy.blackjack.demo.finished_game.domain.model.PlayerProfile;

import java.util.List;

public record PlayerProfileResponseDto(
        Long id,
        String name,
        Long numberOfGamesPlayed,
        Long numberOfGamesWon,
        Long score
) {

    public static PlayerProfileResponseDto from (PlayerProfile playerProfile){
        return new PlayerProfileResponseDto(
                playerProfile.getId(),
                playerProfile.getName().name(),
                playerProfile.getNumberOfGamesPlayed(),
                playerProfile.getNumberOfGamesWon(),
                playerProfile.getScore()
        );
    }

    public static List<PlayerProfileResponseDto> from (List<PlayerProfile> playerProfiles){
        return playerProfiles.stream().map(PlayerProfileResponseDto::from).toList();
    }
}
