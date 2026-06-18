package cat.itacademy.blackjack.game.domain.model;

import cat.itacademy.blackjack.game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.game.domain.value_object.Card;
import cat.itacademy.blackjack.common.domain.value_object.Name;

import java.util.ArrayList;
import java.util.List;

public class UserPlayer extends Player {
    private final Name name;

    public UserPlayer(Name name, Hand handCards) {
        super(handCards);
        if (name == null){
            throw new InvalidPlayerException("name cannot be null");
        }
        this.name = name;
    }

    public static UserPlayer create(Name name) {
        return new UserPlayer(
                name,
                Hand.create()
        );
    }

    public Name getName() {
        return name;
    }
}
