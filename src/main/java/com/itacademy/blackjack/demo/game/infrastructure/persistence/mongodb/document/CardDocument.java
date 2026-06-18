package cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class CardDocument {

    private String number;
    private String suit;

    public CardDocument(String number, String suit) {
        this.number = number;
        this.suit = suit;
    }

    public String getNumber() {
        return number;
    }

    public String getSuit() {
        return suit;
    }
}
