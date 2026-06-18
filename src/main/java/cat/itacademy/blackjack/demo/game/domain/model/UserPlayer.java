package cat.itacademy.blackjack.demo.game.domain.model;

import cat.itacademy.blackjack.demo.game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;

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
