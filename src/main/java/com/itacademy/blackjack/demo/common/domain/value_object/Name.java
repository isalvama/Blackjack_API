package cat.itacademy.blackjack.common.domain.value_object;

import cat.itacademy.blackjack.common.domain.exception.InvalidNameException;

public record Name(String value) {
    public Name {
        if (value == null){
            throw new InvalidNameException("Name cannot be null");
        }

        if (value.isBlank()){
            throw new InvalidNameException("Name cannot be blank");
        }

        if ( value.length() > 50){
            throw new InvalidNameException("Name cannot have more than 50 characters");
        }
    }

    public static Name of(String name){
        return new Name(name);
    }
}
