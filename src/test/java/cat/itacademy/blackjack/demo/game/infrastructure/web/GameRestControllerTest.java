package cat.itacademy.blackjack.demo.game.infrastructure.web;

import cat.itacademy.blackjack.demo.game.application.port.in.CreateGameUseCase;
import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.Suit;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.CardDto;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.CreateGameDto;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.GameResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(GameRestController.class)
class GameRestControllerTest {
    private static final String ID = UUID.randomUUID().toString();
    private static final String NAME = "Player Name";
    private static final GameResponseDto GAME_RESPONSE_DTO = new GameResponseDto(
            ID,
            LocalDateTime.now(),
            LocalDateTime.now(),
            NAME,
            21,
            List.of(new CardDto("ACE", "CLUBS"), new CardDto("JACK", "CLUBS")),
            new Card(CardNumber.EIGHT, Suit.DIAMONDS),
            "STARTED",
            null,
            null
    );


    private static final String API_URL = "/api/blackjack";

    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateGameUseCase createGameUseCase;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("PUT /api/blackjack")
    class CreateFruit {

        @Test
        void shouldCreateNewGameSuccessfully() throws Exception {
            String name = "Player Name";
            CreateGameDto createGameDto = new CreateGameDto("Player Name");

            when(createGameUseCase.execute(name)).thenReturn(GAME_RESPONSE_DTO);

            ResultActions result = mockMvc.perform(post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createGameDto)));

            result.andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/api/blackjack/" + ID)))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                    .andExpect(jsonPath("$.username").value(NAME))
                    .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                    .andExpect(jsonPath("$.playerHand", hasSize(2)))
                    .andExpect(jsonPath("$.dealerFirstCard").exists())
                    .andExpect(jsonPath("$.gameState").value("STARTED"))
                    .andExpect(jsonPath("$.gameResult", anyOf(is(nullValue()))))
                    .andExpect(jsonPath("$.finishedWithBlackjack", anyOf(is(nullValue()))));
            verify(createGameUseCase).execute(NAME);
        }

        @Test
        void shouldReturn400ValidationErrorInBodyDataBlankName() throws Exception {
            CreateGameDto createGameDto = new CreateGameDto(" ");

            ResultActions result = mockMvc.perform(post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createGameDto)));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", Matchers.containsString("Validation Error In Body Data")))
                    .andExpect(jsonPath("$.errors.name", containsString("must not be blank")));

            verifyNoInteractions(createGameUseCase);
        }

        @Test
        void shouldReturn400ValidationErrorInBodyDataNameTooLong() throws Exception {
            String longName = "a".repeat(31);
            CreateGameDto createGameDto = new CreateGameDto(longName);

            ResultActions result = mockMvc.perform(post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createGameDto)));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", Matchers.containsString("Validation Error In Body Data")))
                    .andExpect(jsonPath("$.errors.name", containsString("size must be between 1 and 30")));

            verifyNoInteractions(createGameUseCase);
        }
        // TODO BlackjackException
        // TODO EntityConflictException
        // TODO DomainException
    }
}