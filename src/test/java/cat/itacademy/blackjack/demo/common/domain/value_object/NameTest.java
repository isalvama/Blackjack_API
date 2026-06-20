package cat.itacademy.blackjack.demo.common.domain.value_object;

import static org.junit.jupiter.api.Assertions.*;

import cat.itacademy.blackjack.demo.common.domain.exception.InvalidNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NameTest {
    private static final String NAME = "Apple";

    @Nested
    @DisplayName("Name Creation")
    class NameCreation {

        @Test
        void shouldCreateNameWithFactoryMethod() {
            Name name = Name.of(NAME);

            assertNotNull(name);
            assertEquals(NAME, name.name());
        }
    }

    @Nested
    @DisplayName("Name Validation")
    class NameValidation {

        @Test
        void shouldThrowInvalidNameExceptionWhenNameIsNull() {
            Exception exception = assertThrows(InvalidNameException.class, () -> {
                Name.of(null);
            });
            assertTrue(exception.getMessage().contains("Name"));
            assertTrue(exception.getMessage().contains("null"));

        }

        @Test
        void shouldThrowInvalidNameExceptionWhenNameIsBlank() {
            Exception exception = assertThrows(InvalidNameException.class, () -> {
                Name.of("");
            });
            assertTrue(exception.getMessage().contains("Name"));
            assertTrue(exception.getMessage().contains("blank"));

        }

        @Test
        void shouldThrowInvalidNameExceptionWhenNameSizeIsGreaterThan30() {
            String hundredLettersWord = "a".repeat(31);
            Exception exception = assertThrows(InvalidNameException.class, () -> {
                Name.of(hundredLettersWord);
            });
            assertTrue(exception.getMessage().contains("Name"));
            assertTrue(exception.getMessage().contains("30"));

        }

        @Nested
        @DisplayName("Name format")
        class NameCreation {

            @Test
            void shouldCreateNameWithFactoryMethod() {
                Name fruitName = Name.of(" lowerCASE name ");
                assertEquals("Lowercase Name", fruitName.name());
            }
        }
    }
}