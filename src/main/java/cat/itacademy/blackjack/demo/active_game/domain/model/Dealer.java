package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidPlayerException;

public class Dealer extends Player {
    private Dealer(Hand handCards) {
        super(handCards);
    }

    public static Dealer create(){
        return new Dealer(
                Hand.create()
        );
    }

    public static Dealer reconstitute(Hand hand) {
        if (hand != null && hand.isEmpty()){
            throw new InvalidPlayerException("hand cannot be empty");
        }
        return new Dealer(
                hand
        );
    }

    public boolean shouldStand(){
        return this.getHand().getTotalValue() >= 17 || this.checkBlackjack();
    }
}
