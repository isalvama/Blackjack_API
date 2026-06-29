package cat.itacademy.blackjack.demo.finished_game.domain.model;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.finished_game.domain.exception.InvalidFinishedGameException;
import cat.itacademy.blackjack.demo.finished_game.domain.value_object.HandState;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinishedGameTest {

    private final GameId VALID_GAME_ID = GameId.generate();
    private final HandState PLAYER_HAND = HandState.create(2, 20);
    private final HandState DEALER_HAND = HandState.create(3, 17);
    private final LocalDateTime NOW = LocalDateTime.now();

    @Nested
    class ScoreCalculationTests {

        @Test
        void shouldSetScoreTo21WhenUserWinsWithBlackjack() {
            FinishedGame game = FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, true, NOW);
            assertThat(game.getScore()).isEqualTo(21);
        }

        @Test
        void shouldSetScoreToHandValueWhenUserWinsWithoutBlackjack() {
            FinishedGame game = FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW);
            assertThat(game.getScore()).isEqualTo(PLAYER_HAND.totalCardsValue());
        }

        @Test
        void shouldSetScoreTo11WhenTieWithBlackjack() {
            FinishedGame game = FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.TIE, true, NOW);
            assertThat(game.getScore()).isEqualTo(11);
        }

        @Test
        void shouldSetScoreToHalfHandValueWhenTieWithoutBlackjack() {
            FinishedGame game = FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.TIE, false, NOW);
            assertThat(game.getScore()).isEqualTo(PLAYER_HAND.totalCardsValue() / 2);
        }

        @Test
        void shouldSetScoreToZeroWhenDealerWinsWithoutBlackjack() {
            FinishedGame game = FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.DEALER_WIN, false, NOW);
            assertThat(game.getScore()).isEqualTo(0);
        }

        @Test
        void shouldSetScoreToZeroWhenDealerWinsWithBlackjack() {
            FinishedGame game = FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.DEALER_WIN, true, NOW);
            assertThat(game.getScore()).isEqualTo(0);
        }
    }

    @Nested
    class FactoryTests {

        @Test
        void shouldCreateSuccessfully() {
            FinishedGame game = FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW);
            assertThat(game.getGameId()).isEqualTo(VALID_GAME_ID);
            assertThat(game.getPlayerHandState()).isEqualTo(PLAYER_HAND);
            assertThat(game.getDealerHandState()).isEqualTo(DEALER_HAND);
            assertThat(game.getGameResult()).isEqualTo(GameResult.USER_WIN);
            assertThat(game.getFinishedWithBlackjack()).isEqualTo(false);
            assertThat(game.getCreatedAt()).isNotNull();
        }

        @Test
        void shouldThrowInvalidFinishedGameExceptionWhenCreatingWithNullParams() {
            assertThatThrownBy(() -> FinishedGame.create(null, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("gameId cannot be null");
            assertThatThrownBy(() -> FinishedGame.create(VALID_GAME_ID, null, DEALER_HAND, GameResult.USER_WIN, false, NOW))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("playerHandState cannot be null");
            assertThatThrownBy(() -> FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, null, GameResult.USER_WIN, false, NOW))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("dealerHandState cannot be null");
            assertThatThrownBy(() -> FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, null, false, NOW))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("gameResult cannot be null");
            assertThatThrownBy(() -> FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, null, NOW))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("finishedWithBlackjack cannot be null");
            assertThatThrownBy(() -> FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, null))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("createdAt cannot be null");
        }

        @Test
        void shouldAddPlayerProfileInfo() {
            FinishedGame game = FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW);

            game.addPlayerProfileInfo(123L, new Name("Alice"));

            assertThat(game.getPlayerId()).isEqualTo(123L);
            assertThat(game.getPlayerName().name()).isEqualTo("Alice");
        }

        @Test
        void shouldReconstituteSuccessfully() {
            Long id = 1L;
            Long pId = 99L;
            Name pName = new Name("Bob");
            Integer score = 20;

            FinishedGame game = FinishedGame.reconstitute(id, VALID_GAME_ID, pId, pName, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW, NOW, score);

            assertThat(game.getId()).isEqualTo(id);
            assertThat(game.getGameId()).isEqualTo(VALID_GAME_ID);
            assertThat(game.getPlayerId()).isEqualTo(pId);
            assertThat(game.getPlayerName()).isEqualTo(pName);
            assertThat(game.getScore()).isEqualTo(score);
        }

        @Test
        void shouldThrowInvalidFinishedGameExceptionWhenReconstitutingWithNegativeParams() {
            Long id = 1L;
            Long pId = 99L;
            Name pName = new Name("Bob");
            Integer score = 20;

            assertThatThrownBy(() -> FinishedGame.reconstitute(-1L, VALID_GAME_ID, pId, pName, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW, NOW, score))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("id cannot be negative");
            assertThatThrownBy(() -> FinishedGame.reconstitute(id, VALID_GAME_ID, -1L, pName, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW, NOW, score))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("playerId cannot be negative");
            assertThatThrownBy(() -> FinishedGame.reconstitute(id, VALID_GAME_ID, pId, pName, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW, NOW, -1))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("score cannot be negative");
        }

        @Test
        void shouldThrowInvalidFinishedGameExceptionWhenReconstitutingWithNullParams() {
            Long id = 1L;
            Long pId = 99L;
            Name pName = new Name("Bob");
            Integer score = 20;

            assertThatThrownBy(() -> FinishedGame.reconstitute(null, VALID_GAME_ID, pId, pName, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW, NOW, score))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("id cannot be null");

            assertThatThrownBy(() -> FinishedGame.reconstitute(id, VALID_GAME_ID, null, pName, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW, NOW, score))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("playerId cannot be null");

            assertThatThrownBy(() -> FinishedGame.reconstitute(id, VALID_GAME_ID, pId, null, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW, NOW, score))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("playerName cannot be null");

            assertThatThrownBy(() -> FinishedGame.reconstitute(id, VALID_GAME_ID, pId, pName, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW, null, score))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("finishedAt cannot be null");

            assertThatThrownBy(() -> FinishedGame.reconstitute(id, VALID_GAME_ID, pId, pName, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW, NOW, null))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("score cannot be null");
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void shouldThrowExceptionWhenGameIdIsNull() {
            assertThatThrownBy(() -> FinishedGame.create(null, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW))
                    .isInstanceOf(InvalidFinishedGameException.class)
                    .hasMessageContaining("gameId cannot be null");
        }

        @ParameterizedTest
        @CsvSource({
                "playerId cannot be null",
                "playerName cannot be null"
        })
        void addPlayerInfoShouldValidateNulls(String expectedMessage) {
            FinishedGame game = FinishedGame.create(VALID_GAME_ID, PLAYER_HAND, DEALER_HAND, GameResult.USER_WIN, false, NOW);

            if (expectedMessage.contains("playerId")) {
                assertThatThrownBy(() -> game.addPlayerProfileInfo(null, new Name("Alice")))
                        .isInstanceOf(InvalidFinishedGameException.class)
                        .hasMessageContaining(expectedMessage);
            } else {
                assertThatThrownBy(() -> game.addPlayerProfileInfo(1L, null))
                        .isInstanceOf(InvalidFinishedGameException.class)
                        .hasMessageContaining(expectedMessage);
            }
        }
    }
}