package cat.itacademy.blackjack.demo.common.domain.value_object;

import cat.itacademy.blackjack.demo.common.domain.exception.InvalidNameException;

import java.util.Arrays;
import java.util.stream.Collectors;

public record Name(String name) {
    public Name {
        validate(name);
        name = format(name);
    }

    public static Name of(String name){
        return new Name(name);
    }

    private String format (String name){
        String nameTrimmed = name.trim();
        return Arrays.stream(nameTrimmed.split(" ")).map(word -> word.substring(0, 1).toUpperCase()+word.substring(1).toLowerCase()).collect(Collectors.joining(" "));
    }

    private void validate (String name){
        if (name == null){
            throw new InvalidNameException("Name cannot be null");
        }

        if (name.isBlank()){
            throw new InvalidNameException("Name cannot be blank");
        }

        if (name.length() > 30){
            throw new InvalidNameException("Name cannot have more than 30 characters");
        }
    }
}
