package cat.itacademy.blackjack.game_statistics.domain.model;

import cat.itacademy.blackjack.game_statistics.domain.exception.InvalidPlayerProfileException;
import cat.itacademy.blackjack.common.domain.value_object.Name;

import java.util.ArrayList;
import java.util.List;

public class PlayerProfile {
    private Long id;
    private Name name;
    private Integer numberOfGamesWon;
    private Integer numberOfGamesPlayed;
    private Long score;
//    private List<FinishedGame> playedGames;

    public PlayerProfile(Long id, Name name, Integer numberOfGamesWon, Integer numberOfGamesPlayed, Long score) {
        if (name == null){
            throw new InvalidPlayerProfileException("name cannot be null");
        }
        if (numberOfGamesWon == null){
            throw new InvalidPlayerProfileException("numberOfGamesWon cannot be null");
        }
        if (numberOfGamesWon < 0){
            throw new InvalidPlayerProfileException("numberOfGamesWon cannot be negative");
        }
        if (numberOfGamesPlayed == null){
            throw new InvalidPlayerProfileException("numberOfGamesPlayed cannot be null");
        }
        if (numberOfGamesPlayed < 0){
            throw new InvalidPlayerProfileException("numberOfGamesPlayed cannot be negative");
        }
        if (score == null){
            throw new InvalidPlayerProfileException("score cannot be null");
        }
        if (score < 0L){
            throw new InvalidPlayerProfileException("score cannot be negative");
        }
//        if (playedGames == null){
//            throw new InvalidPlayerProfileException("playedGames list cannot be null");
//        }
        this.id = id;
        this.name = name;
        this.numberOfGamesWon = numberOfGamesWon;
        this.numberOfGamesPlayed = numberOfGamesPlayed;
        this.score = score;
//        this.playedGames = playedGames;
    }

    public static PlayerProfile create (Name name){
        return new PlayerProfile(
                null,
                name,
                0,
                0,
                0L
//                new ArrayList<>()
        );
    }
    public void updateProfileWithNewGame(){
        this.numberOfGamesWon++;
        this.numberOfGamesPlayed++;
    }

    public Name getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Integer getNumberOfGamesWon() {
        return numberOfGamesWon;
    }

    public Integer getNumberOfGamesPlayed() {
        return numberOfGamesPlayed;
    }

    public Long getScore() {
        return score;
    }

//    public List<FinishedGame> getPlayedGames() {
//        return List.copyOf(playedGames);
//    }
}
