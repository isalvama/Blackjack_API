package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidGameException;
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
            throw new InvalidGameException("the card hit cannot be null");
        }
        this.hand.addCard(card);
    }

    boolean canChangeAceValue(){
        return hand.valueIsGreaterThan21() && hand.hasAce();
    }

    boolean totalValueIsGreaterThan21(){
        return this.hand.valueIsGreaterThan21();
    }

    void changeAceValueToOne(){
        this.hand.changeAceValueToOne();
    }

    boolean checkBlackjack(){
        return this.hand.isBlackjack();
    }

    void setCardsValueToTwentyOne(){
        this.hand.setCardsValueToTwentyOne();
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
