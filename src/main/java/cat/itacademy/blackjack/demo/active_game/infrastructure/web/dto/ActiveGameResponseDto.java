package cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto;

import cat.itacademy.blackjack.demo.active_game.domain.model.Game;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record ActiveGameResponseDto(
        @Schema(description = "Generated if of the game.", example = "3d558128-622e-47dd-91b9-54d2b93aed21")
        String id,

        @Schema(description = "Game creation timestamp.", example = "2026-07-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp of the player's last interaction with the game.", example = "2026-07-01T10:35:00")
        LocalDateTime lastTimePlayedAt,

        @Schema(description = "Name of the player",example = "John Doe")
        String username,

        @Schema(description = "Total value of player's cards", example = "15")
        Integer playerTotalCardsValue,

        @Schema(description = "The collection of cards currently held by the player",
        example = """
        [
          { "suit": "SPADES", "cardNumber": "ACE" },
          { "suit": "HEARTS", "cardNumber": "TEN" }
        ]
        """)
        List<CardDto> playerHand,

        @Schema(description = "The first card that it was given to the dealer",
                example = "{ \"suit\": \"DIAMONDS\", \"cardNumber\": \"KING\" }")
        CardDto dealerFirstCard,

        @Schema(description = "Total value of dealer's cards", example = "17")
        Integer dealerTotalCardsValue,

        @Schema(description = "The current status of the game (if it is active or over)",
                allowableValues = {"STARTED", "OVER"},
                example = "STARTED")
        String gameState,

        @Schema(description = "The final outcome of the game. Remaining null while the game is in progress.",
                allowableValues = {"USER_WIN", "DEALER_WIN", "TIE"},
                example = "TIE")
        String gameResult,

        @Schema(
                description = "Indicates if the game ended with a natural Blackjack (21 with the first two cards).",
                example = "true"
        )
        Boolean finishedWithBlackjack,

        @Schema(
                description = "The dealer's complete hand, revealed only after the game has finished.",
                example = """
        [
          { "suit": "CLUBS", "cardNumber": "KING" },
          { "suit": "DIAMONDS", "cardNumber": "JACK" }
        ]
        """
        )
        List<CardDto> dealerFinalHand
) {
    public static ActiveGameResponseDto from (Game game){
        var outcome = game.getGameOutcome();

        return new ActiveGameResponseDto(
                game.getId().value().toString(),
                game.getCreatedAt(),
                game.getLastTimePlayedAt(),
                game.getUserPlayer().getName().name(),
                game.getUserPlayer().getHandValue(),
                game.getUserPlayer().getHand().getCards().stream().map(CardDto::from).toList(),
                CardDto.from(game.getDealer().getHand().getCards().getFirst()),
                game.getDealer().getHandValue(),
                game.getGameState().name(),
                outcome != null ? game.getGameOutcome().result().name() : null,
                outcome != null ? game.getGameOutcome().blackjack() : null,
                outcome != null ? game.getDealer().getHand().getCards().stream().map(CardDto::from).toList() : null
        );
    }

    public static List<ActiveGameResponseDto> from (List<Game> games){
        if (games == null || games.isEmpty()){
            return List.of();
        }
        return games.stream().map(ActiveGameResponseDto::from).toList();
    }

    }
