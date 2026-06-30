package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidActiveGameException;
import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;

public abstract class Player {
    private Hand hand;

    protected Player(Hand hand) {

        if (hand == null){
            throw new InvalidPlayerException("playerHand cannot be null");
        }
        this.hand = hand;
    }

    public void hit(Card card){
        if (card == null){
            throw new InvalidActiveGameException("the card hit cannot be null");
        }
        this.hand.addCard(card);
    }


    boolean totalValueIsGreaterThan21(){
        return this.hand.valueIsGreaterThan21();
    }

    boolean checkBlackjack(){
        return hand.isBlackjack();
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
