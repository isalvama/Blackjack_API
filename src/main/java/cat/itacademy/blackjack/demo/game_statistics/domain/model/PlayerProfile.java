package cat.itacademy.blackjack.demo.game_statistics.domain.model;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.game_statistics.domain.exception.InvalidPlayerProfileException;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import lombok.Getter;

@Getter
public class PlayerProfile {
    private Long id;
    private Name name;
    private Long numberOfGamesPlayed;
    private Long numberOfGamesWon;
    private Long score;

    private PlayerProfile(Name name, Long numberOfGamesPlayed, Long numberOfGamesWon, Long score) {

        this.name = validateNotNull(name, "name cannot be null");
        this.numberOfGamesPlayed = validateNonNullPositiveLong(numberOfGamesPlayed, "numberOfGamesPlayed");
        this.numberOfGamesWon = validateNonNullPositiveLong(numberOfGamesWon, "numberOfGamesWon");
        this.score = validateNonNullPositiveLong(score, "score");
    }

    public static PlayerProfile create (Name name){
        return new PlayerProfile(
                name,
                0L,
                0L,
                0L
        );
    }

    public static PlayerProfile reconstitute (Long id, Name name,Long numberOfGamesPlayed, Long numberOfGamesWon, Long score){
        PlayerProfile playerProfile = new PlayerProfile(
                name,
                numberOfGamesWon,
                numberOfGamesPlayed,
                score
        );
        playerProfile.id = validateNonNullPositiveLong(id, "id");
        return playerProfile;
    }

    public void updateProfileWithNewGame(String gameResult, Long totalCardsValue, Boolean finishedWithBlackJack){
        this.numberOfGamesWon++;
        this.numberOfGamesPlayed++;

        if (gameResult.equalsIgnoreCase(GameResult.USER_WIN.name()) && finishedWithBlackJack){
            this.score += 21;
        }
        if (gameResult.equalsIgnoreCase(GameResult.USER_WIN.name()) && !finishedWithBlackJack){
            this.score += totalCardsValue;
        }
        if (gameResult.equalsIgnoreCase(GameResult.TIE.name()) && finishedWithBlackJack){
            this.score += 11;
        }
        if (gameResult.equalsIgnoreCase(GameResult.TIE.name()) && !finishedWithBlackJack){
            this.score += totalCardsValue/2;
        }
    }

    private static <T> T validateNotNull(T obj, String message) {
        if (obj == null)
            throw new InvalidPlayerProfileException(message);
        return obj;
    }

    private static Long validateNonNullPositiveLong(Long longValue, String fieldName) {
        validateNotNull(longValue, String.format("%s cannot be null", fieldName));
        if (longValue < 0L){
            throw new InvalidPlayerProfileException(String.format("%s cannot be negative", fieldName));
        }
        return longValue;
    }
}
