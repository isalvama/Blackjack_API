package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import lombok.Getter;

@Getter
public class UserPlayer extends Player {
    private final Name name;

    private UserPlayer(Name name, Hand handCards) {
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

    public static UserPlayer reconstitute(Name name, Hand hand) {
        if (hand != null && hand.isEmpty()){
            throw new InvalidPlayerException("hand cannot be empty");
        }
        return new UserPlayer(
                name,
                hand
        );
    }

}
