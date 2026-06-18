package cat.itacademy.blackjack.demo.game.domain.model;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidGameException;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;
    private Integer totalValue;

    public Hand(List<Card> cards, Integer totalValue){
        if (cards == null){
            throw new InvalidPlayerException("cards cannot be null");
        }
        if (totalValue == null){
            throw new InvalidPlayerException("total value cannot be null");
        }
        this.cards = cards;
        this.totalValue = totalValue;
    }

    public static Hand create (){
        return new Hand(
                new ArrayList<Card>(),
                0
        );
    }

    public void addCard (Card card){
        if (card == null){
            throw new InvalidGameException("card to hit cannot be null");
        }
        if (this.cards.contains(card)) {
            throw new InvalidGameException("a player cannot take a repeated card");
        } else {
            this.cards.add(card);
            this.totalValue += card.cardNumber().getValue();
        }
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }

    public Integer getTotalValue() {
        return totalValue;
    }
}
