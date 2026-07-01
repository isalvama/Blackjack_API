package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.player_profiles.dto;

import cat.itacademy.blackjack.demo.finished_game.domain.model.PlayerProfile;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detailed statistics and ranking information for a player profile")
public record PlayerProfileResponseDto(
        @Schema(description = "Unique database identifier for the player", example = "1")
        Long id,

        @Schema(description = "Registered username of the player", example = "John Doe")
        String name,

        @Schema(description = "Total number of blackjack games this player has completed", example = "42")
        Long numberOfGamesPlayed,

        @Schema(description = "Total number of games won by the player", example = "15")
        Long numberOfGamesWon,

        @Schema(description = "Cumulative score calculated from all game outcomes", example = "1250")
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
