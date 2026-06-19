package cat.itacademy.blackjack.demo.game.domain.model;

import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidGameException;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;

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
        return this.hand.isBlackjack();
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
