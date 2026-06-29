package cat.itacademy.blackjack.demo.finished_game.domain.model;

import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.finished_game.domain.exception.InvalidPlayerProfileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerProfileTest {

    private final Name VALID_NAME = new Name("Alice");

    @Nested
    class Create {

        @Test
        void shouldCreateProfileWithInitialValues() {
            PlayerProfile profile = PlayerProfile.create(VALID_NAME);

            assertThat(profile.getName()).isEqualTo(VALID_NAME);
            assertThat(profile.getNumberOfGamesPlayed()).isZero();
            assertThat(profile.getNumberOfGamesWon()).isZero();
            assertThat(profile.getScore()).isZero();
            assertThat(profile.getId()).isNull();
        }

        @Test
        void shouldThrowExceptionWhenNameIsNull() {
            assertThatThrownBy(() -> PlayerProfile.create(null))
                    .isInstanceOf(InvalidPlayerProfileException.class)
                    .hasMessageContaining("name cannot be null");
        }
    }

    @Nested
    class Reconstitute {

        @Test
        void shouldReconstituteSuccessfully() {
            Long id = 1L;
            Long played = 10L;
            Long won = 5L;
            Long score = 500L;

            PlayerProfile profile = PlayerProfile.reconstitute(id, VALID_NAME, played, won, score);

            assertThat(profile.getId()).isEqualTo(id);
            assertThat(profile.getName()).isEqualTo(VALID_NAME);
            assertThat(profile.getNumberOfGamesPlayed()).isEqualTo(played);
            assertThat(profile.getNumberOfGamesWon()).isEqualTo(won);
            assertThat(profile.getScore()).isEqualTo(score);
        }

        @ParameterizedTest
        @CsvSource({
                "-1, Alice, 10, 5, 100, id cannot be negative",
                "1, Alice, -1, 5, 100, numberOfGamesPlayed cannot be negative",
                "1, Alice, 10, -1, 100, numberOfGamesWon cannot be negative",
                "1, Alice, 10, 5, -1, score cannot be negative"
        })
        void shouldValidateNegativeValues(Long id, String name, Long played, Long won, Long score, String expectedMessage) {
            Name playerName = new Name(name);
            assertThatThrownBy(() -> PlayerProfile.reconstitute(id, playerName, played, won, score))
                    .isInstanceOf(InvalidPlayerProfileException.class)
                    .hasMessageContaining(expectedMessage);
        }
    }

    @Nested
    class UpdateTests {

        @Test
        void shouldUpdateNumberOfGamesAndScoreCorrectly() {
            PlayerProfile profile = PlayerProfile.reconstitute(1L, VALID_NAME, 5L, 2L, 100L);

            profile.updateProfileWithNewGame(50);

            assertThat(profile.getNumberOfGamesPlayed()).isEqualTo(6L);
            assertThat(profile.getNumberOfGamesWon()).isEqualTo(3L);
            assertThat(profile.getScore()).isEqualTo(150L);
        }

        @Test
        void shouldUpdateNewProfile() {
            PlayerProfile profile = PlayerProfile.create(VALID_NAME);

            profile.updateProfileWithNewGame(21);

            assertThat(profile.getNumberOfGamesPlayed()).isEqualTo(1L);
            assertThat(profile.getNumberOfGamesWon()).isEqualTo(1L);
            assertThat(profile.getScore()).isEqualTo(21L);
        }
    }
}