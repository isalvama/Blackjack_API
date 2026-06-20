package cat.itacademy.blackjack.demo.game_statistics.domain.value_object;

import cat.itacademy.blackjack.demo.game_statistics.domain.exception.InvalidFinishedGameException;

public record HandState(
         Integer numberOfRequestedCards,
         Integer totalCardsValue

) {
    public HandState{
        if (numberOfRequestedCards == null){
            throw new InvalidFinishedGameException("numberOfRequestedCards cannot be null");
        }
        if (numberOfRequestedCards < 0){
            throw new InvalidFinishedGameException("numberOfRequestedCards cannot be negative");
        }
        if (totalCardsValue == null){
            throw new InvalidFinishedGameException("totalCardsValue cannot be null");
        }
        if (totalCardsValue < 1){
            throw new InvalidFinishedGameException("totalCardsValue cannot be null");
        }
    }

    public static HandState create (Integer numberOfRequestedCards,
                                    Integer totalCardsValue){
        return new HandState(
                numberOfRequestedCards,
                totalCardsValue
        );
    }
}
