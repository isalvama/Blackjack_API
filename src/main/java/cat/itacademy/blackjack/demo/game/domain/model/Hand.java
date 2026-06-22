package cat.itacademy.blackjack.demo.game.domain.model;
import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidGameException;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidHandException;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;
    @Getter
    private Integer totalValue;

    private Hand(List<Card> cards, Integer totalValue){
        if (cards == null){
            throw new InvalidHandException("cards cannot be null");
        }
        if (totalValue == null){
            throw new InvalidHandException("total value cannot be null");
        }
        this.cards = new ArrayList<>(cards);
        this.totalValue = totalValue;
    }

    public static Hand create (){
        return new Hand(
                new ArrayList<>(),
                0
        );
    }

    public static Hand reconstitute (List<Card> cards, Integer totalValue){
        return new Hand(
                cards,
                totalValue
        );
    }

     void addCard (Card card){
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

    boolean isBlackjack(){
        if (this.getCards().size() != 2){
            return false;
        }
        List<Card> firstTwoCards = this.getCards().subList(0, 2);
        boolean hasJack = firstTwoCards.stream().anyMatch(c -> c.cardNumber().equals(CardNumber.JACK)
                || c.cardNumber().equals(CardNumber.KING) || c.cardNumber().equals(CardNumber.QUEEN));
        return hasAce() && hasJack;
    }

    void setCardsValueToTwentyOne(){
        this.totalValue = 21;
    }

    boolean isEmpty(){
        return this.cards.isEmpty();
    }

    boolean hasAce (){
        return cards.stream().anyMatch(c -> c.cardNumber().equals(CardNumber.ACE));
    }

    boolean valueIsGreaterThan21(){
        return this.totalValue > 21;
    }

    void changeAceValueToOne () {
       long numberOfAce = cards.stream().filter(c -> c.cardNumber().equals(CardNumber.ACE)).count();
       this.totalValue -= Math.toIntExact(numberOfAce * 10);
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }
}
