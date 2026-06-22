package cat.itacademy.blackjack.demo.active_game.infrastructure.web;

import cat.itacademy.blackjack.demo.active_game.application.port.in.CreateGameUseCase;
import cat.itacademy.blackjack.demo.active_game.application.port.in.GetActiveGameUseCase;
import cat.itacademy.blackjack.demo.active_game.application.port.in.HitUseCase;
import cat.itacademy.blackjack.demo.active_game.application.port.in.StandUseCase;
import cat.itacademy.blackjack.demo.active_game.domain.GameState;
import cat.itacademy.blackjack.demo.active_game.domain.exception.GameNotFoundException;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.CardDto;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.CreateGameDto;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.GameResponseDto;
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


@WebMvcTest(ActiveGameRestController.class)
class GameRestControllerTest {
    private static final String ID = UUID.randomUUID().toString();
    private static final String NAME = "Player Name";
    private static String GAME_STATE_STARTED = GameState.STARTED.toString();
    private static String GAME_STATE_OVER = GameState.OVER.toString();
    private static final GameResponseDto GAME_RESPONSE_DTO_STARTED = new GameResponseDto(
            ID,
            LocalDateTime.now(),
            LocalDateTime.now(),
            NAME,
            21,
            List.of(new CardDto("ACE", "CLUBS"), new CardDto("JACK", "CLUBS")),
            new CardDto("EIGHT", "DIAMONDS"),
            8,
            GAME_STATE_STARTED,
            null,
            null,
            null
    );

    private static final GameResponseDto GAME_RESPONSE_DTO_OVER = new GameResponseDto(
            ID,
            LocalDateTime.now(),
            LocalDateTime.now(),
            NAME,
            21,
            List.of(new CardDto("ACE", "CLUBS"), new CardDto("EIGHT", "CLUBS")),
            new CardDto("EIGHT", "DIAMONDS"),
            8,
            GAME_STATE_OVER,
            "USER_WIN",
            false,
            List.of(new CardDto("EIGHT", "HEARTS"), new CardDto("TWO", "HEARTS"))
    );

    private static final String API_URL = "/api/active-games";

    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateGameUseCase createGameUseCase;

    @MockitoBean
    private GetActiveGameUseCase getActiveGameUseCase;

    @MockitoBean
    private HitUseCase hitUseCase;

    @MockitoBean
    private StandUseCase standUseCase;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("PUT /api/blackjack")
    class CreateNewGame {

        @Test
        void shouldCreateNewGameSuccessfully() throws Exception {
            String name = "Player Name";
            CreateGameDto createGameDto = new CreateGameDto("Player Name");

            when(createGameUseCase.execute(name)).thenReturn(GAME_RESPONSE_DTO_STARTED);

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
    @DisplayName("GET /api/blackjack")
    class GetActiveGameState {

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

            when(getActiveGameUseCase.execute(ID)).thenReturn(GAME_RESPONSE_DTO_STARTED);

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

            when(getActiveGameUseCase.execute(ID)).thenThrow(new GameNotFoundException(ID));

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + ID)
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", containsString("Game")))
                    .andExpect(jsonPath("$.detail", containsString("not")))
                    .andExpect(jsonPath("$.detail", containsString("found")))
                    .andExpect(jsonPath("$.detail", containsString(ID)));
        }
    }
    @Nested
    @DisplayName("POST /api/blackjack/{id}/hit")
    class Hit {

        @Test
        void shouldReturn400ValidationErrorInvalidId() throws Exception {
            String invalidId = "invalid-id";

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + invalidId + "/hit")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", Matchers.containsString("Validation Error in Parameter")));

            verifyNoInteractions(standUseCase);
        }

        @Test
        void shouldReturnActiveGameInfoData() throws Exception {

            when(standUseCase.execute(ID)).thenReturn(GAME_RESPONSE_DTO_STARTED);

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + ID + "/hit")
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

            verify(hitUseCase, times(1)).execute(ID);
        }

        @DisplayName("should return 404 Game Not Found when the service throws a GameNotFoundException")
        @Test
        void shouldReturn404NotFound() throws Exception {

            when(hitUseCase.execute(ID)).thenThrow(new GameNotFoundException(ID));

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + ID + "/hit")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", containsString("Game")))
                    .andExpect(jsonPath("$.detail", containsString("not")))
                    .andExpect(jsonPath("$.detail", containsString("found")))
                    .andExpect(jsonPath("$.detail", containsString(ID)));
        }
    }

    @Nested
    @DisplayName("POST /api/blackjack/{id}/stand")
    class Stand {

        @Test
        void shouldReturn400ValidationErrorInvalidId() throws Exception {
            String invalidId = "invalid-id";

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + invalidId + "/stand")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", Matchers.containsString("Validation Error in Parameter")));

            verifyNoInteractions(standUseCase);
        }

        @Test
        void shouldReturnActiveGameInfoData() throws Exception {

            when(standUseCase.execute(ID)).thenReturn(GAME_RESPONSE_DTO_OVER);

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + ID + "/stand")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                    .andExpect(jsonPath("$.username").value(NAME))
                    .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                    .andExpect(jsonPath("$.playerHand", hasSize(2)))
                    .andExpect(jsonPath("$.dealerFirstCard").exists())
                    .andExpect(jsonPath("$.gameState").value(GAME_STATE_OVER))
                    .andExpect(jsonPath("$.gameResult").exists())
                    .andExpect(jsonPath("$.finishedWithBlackjack").exists())
                    .andExpect(jsonPath("$.dealerFinalHand").exists());


            verify(standUseCase, times(1)).execute(ID);
        }

        @DisplayName("should return 404 Game Not Found when the service throws a GameNotFoundException")
        @Test
        void shouldReturn404NotFound() throws Exception {

            when(standUseCase.execute(ID)).thenThrow(new GameNotFoundException(ID));

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + ID + "/stand")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", containsString("Game")))
                    .andExpect(jsonPath("$.detail", containsString("not")))
                    .andExpect(jsonPath("$.detail", containsString("found")))
                    .andExpect(jsonPath("$.detail", containsString(ID)));
        }
    }

    // TODO BlackjackException
    // TODO EntityConflictException
    // TODO DomainException
}