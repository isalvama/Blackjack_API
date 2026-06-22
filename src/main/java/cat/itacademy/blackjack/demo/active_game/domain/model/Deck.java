package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.active_game.application.service.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.active_game.domain.CardNumber;
import cat.itacademy.blackjack.demo.active_game.domain.Suit;
import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidDeckException;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    List<Card> cards;

    private Deck(List<Card> cards) {
        if (cards == null) {
            throw new InvalidDeckException("cards list cannot be null");
        }
        if (cards.isEmpty()) {
            throw new InvalidDeckException("cards list cannot be empty");
        }
        this.cards = new ArrayList<>(cards);
    }

    public void shuffle (ShuffleStrategy shuffleStrategy){
        shuffleStrategy.shuffle(this.cards);
    }

    public static Deck create(){
        List<Card> cards = new ArrayList<>();
        for (Suit suit : Suit.values()){
            for (CardNumber cardNumber : CardNumber.values()){
                cards.add(new Card(cardNumber, suit));
            }
        }
        return new Deck(cards);
    }
    public static Deck from (List<Card> cards){
        return new Deck(cards);
    }

    public boolean hasNoCards(){
       return this.cards.isEmpty();
    }

    public Card draw() {
        Card card = cards.getFirst();
        cards.removeFirst();
        return card;
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }
}
