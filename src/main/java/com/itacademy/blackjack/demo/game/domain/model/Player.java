package cat.itacademy.blackjack.game.domain.model;

import cat.itacademy.blackjack.game.domain.CardNumber;
import cat.itacademy.blackjack.game.domain.exception.InvalidGameException;
import cat.itacademy.blackjack.game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.game.domain.value_object.Card;
import lombok.Getter;

import java.util.List;

public abstract class Player {
    private Hand hand;

    public Player(Hand hand) {

        if (hand == null){
            throw new InvalidPlayerException("hand cannot be null");
        }
        this.hand = hand;
    }

    void hit(Card card){
        if (card == null){
            throw new InvalidGameException("the card hit cannot be null");
        }
        this.hand.addCard(card);
    }

    boolean checkBlackjack(){
        if (this.hand.getCards().size() != 2){
            return false;
        }
        List<Card> firstTwoCards = this.hand.getCards().subList(0, 2);
        boolean hasAce = firstTwoCards.stream().anyMatch(c -> c.cardNumber().equals(CardNumber.ACE));
        boolean hasJack = firstTwoCards.stream().anyMatch(c -> c.cardNumber().equals(CardNumber.JACK)
                || c.cardNumber().equals(CardNumber.KING) || c.cardNumber().equals(CardNumber.QUEEN));
        return hasAce && hasJack;
    }

    public Hand getHand() {
        return this.hand;
    }

    public int getHandValue() {
       return this.hand.getTotalValue();
    }

    public int getNumberOfCards() {
        return this.hand.getCards().size();
    }

}
