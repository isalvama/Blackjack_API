package cat.itacademy.blackjack.demo.game.infrastructure.web;

import cat.itacademy.blackjack.demo.game.application.port.in.CreateGameUseCase;
import cat.itacademy.blackjack.demo.game.application.port.in.GetActiveGameUseCase;
import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.GameState;
import cat.itacademy.blackjack.demo.game.domain.Suit;
import cat.itacademy.blackjack.demo.game.domain.exception.GameNotFoundException;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    private static String GAME_STATE_STARTED = GameState.STARTED.toString();
    private static final GameResponseDto GAME_RESPONSE_DTO = new GameResponseDto(
            ID,
            LocalDateTime.now(),
            LocalDateTime.now(),
            NAME,
            21,
            List.of(new CardDto("ACE", "CLUBS"), new CardDto("JACK", "CLUBS")),
            new Card(CardNumber.EIGHT, Suit.DIAMONDS),
            GAME_STATE_STARTED,
            null,
            null
    );

    private static final String API_URL = "/api/blackjack";

    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateGameUseCase createGameUseCase;

    @MockitoBean
    private GetActiveGameUseCase getActiveGameUseCase;

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
                    .andExpect(jsonPath("$.gameState").value(GAME_STATE_STARTED))
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
    }

    @Nested
    class getActiveGameState {

        @Test
        void shouldReturn400ValidationErrorInvalidId() throws Exception {
            String invalidId = "invalid-id";

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + invalidId)
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", Matchers.containsString("Validation Error in Parameter")));

            verifyNoInteractions(createGameUseCase);
        }

        @Test
        void shouldReturnActiveGameInfoData() throws Exception {

            when(getActiveGameUseCase.execute(ID)).thenReturn(GAME_RESPONSE_DTO);

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + ID)
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                    .andExpect(jsonPath("$.username").value(NAME))
                    .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                    .andExpect(jsonPath("$.playerHand", hasSize(2)))
                    .andExpect(jsonPath("$.dealerFirstCard").exists())
                    .andExpect(jsonPath("$.gameState").value(GAME_STATE_STARTED))
                    .andExpect(jsonPath("$.gameResult", anyOf(is(nullValue()))))
                    .andExpect(jsonPath("$.finishedWithBlackjack", anyOf(is(nullValue()))));

            verify(getActiveGameUseCase, times(1)).execute(ID);
        }

        @DisplayName("should return 404 Game Not Found when the service throws a GameNotFoundException")
        @Test
        void shouldReturn404NotFound() throws Exception {

            when(getActiveGameUseCase.execute(ID)).thenThrow(new GameNotFoundException("no games found with id " + ID));

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + ID)
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", containsString("Game Not Found")))
                    .andExpect(jsonPath("$.detail", containsString("games")))
                    .andExpect(jsonPath("$.detail", containsString("found")))
                    .andExpect(jsonPath("$.detail", containsString("id")))
                    .andExpect(jsonPath("$.detail", containsString(ID)));
        }
    }

    // TODO BlackjackException
    // TODO EntityConflictException
    // TODO DomainException
}