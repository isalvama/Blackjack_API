package cat.itacademy.blackjack.demo.game.domain.model;

public class Dealer extends Player{
    public Dealer(Hand handCards) {
        super(handCards);
    }

    public static Dealer create(){
        return new Dealer(
                Hand.create()
        );
    }

    public boolean shouldStand(){
        return this.getHand().getTotalValue() >= 17 || this.checkBlackjack();
    }
}
