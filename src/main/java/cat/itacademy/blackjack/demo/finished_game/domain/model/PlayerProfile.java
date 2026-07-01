package cat.itacademy.blackjack.demo.finished_game.domain.model;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.finished_game.domain.exception.InvalidPlayerProfileException;
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
                numberOfGamesPlayed,
                numberOfGamesWon,
                score
        );
        playerProfile.id = validateNonNullPositiveLong(id, "id");
        return playerProfile;
    }

    public void updateProfileWithNewGame(GameResult gameResult, Integer gameScore){
        this.numberOfGamesPlayed++;
        this.score += gameScore;
        if (gameResult == GameResult.USER_WIN) {
            this.numberOfGamesWon++;
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
