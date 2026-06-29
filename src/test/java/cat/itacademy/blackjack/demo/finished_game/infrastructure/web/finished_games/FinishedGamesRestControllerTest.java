package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGameUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGamesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortType;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games.dto.FinishedGamesResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(FinishedGamesRestController.class)
class FinishedGamesRestControllerTest {

    private FinishedGamesResponseDto createMockDto(String gameId) {
        return new FinishedGamesResponseDto(
                1L,
                gameId,
                10L,
                "TestPlayer",
                2,
                3,
                20,
                18,
                "USER_WIN",
                false,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now(),
                55
        );
    }

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private GetFinishedGamesSortedUseCase getFinishedGamesUseCase;

        @MockitoBean
        private GetFinishedGameUseCase getFinishedGameUseCase;

        private final String API_URL = "/api/finished-games";

        @Nested
        @DisplayName("GET " + API_URL + "/{id}")
        class GetGameById {

            @Test
            void shouldReturn200WhenIdIsValid() throws Exception {
                String validUuid = UUID.randomUUID().toString();
                FinishedGamesResponseDto mockResponse = createMockDto(validUuid);

                when(getFinishedGameUseCase.getGameById(validUuid)).thenReturn(mockResponse);

                mockMvc.perform(get(API_URL + "/{id}", validUuid))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.gameId").value(validUuid))
                        .andExpect(jsonPath("$.playerName").value("TestPlayer"));

                verify(getFinishedGameUseCase).getGameById(any(String.class));
            }

            @Test
            void shouldReturn400WhenIdIsInvalid() throws Exception {
                String invalidUuid = "not-a-uuid";

                mockMvc.perform(get(API_URL + "/{id}", invalidUuid))
                        .andExpect(status().isBadRequest());

                verify(getFinishedGameUseCase, never()).getGameById(any(String.class));
            }
        }

    @Nested
    @DisplayName("GET " + API_URL)
    class GetGames {

        @Test
        void shouldReturnListWithDefaultSort() throws Exception {
            FinishedGamesResponseDto game = createMockDto(UUID.randomUUID().toString());
            when(getFinishedGamesUseCase.execute(any(FinishedGameSortCriteria.class)))
                    .thenReturn(List.of(game));

            mockMvc.perform(get(API_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].playerName").value("TestPlayer"));

            ArgumentCaptor<FinishedGameSortCriteria> captor = ArgumentCaptor.forClass(FinishedGameSortCriteria.class);
            verify(getFinishedGamesUseCase).execute(captor.capture());
            assertThat(captor.getValue().sortType()).isEqualTo(FinishedGameSortType.FINISHED_AT_DESC);
        }

        @Test
        void shouldReturnBadRequestWhenPlayerIdAndPlayerNameAreProvidedAsParams() throws Exception {
            long playerId = 123L;
            String playerName = "Alice";

            mockMvc.perform(get(API_URL)
                            .param("playerId", Long.toString(playerId))
                            .param("playerName", playerName)
                            .param("sort", "SCORE_DESC"))
                    .andExpect(status().isBadRequest());

            verify(getFinishedGamesUseCase, never()).execute(any());
        }

        @Test
        void shouldReturn400WhenSortIsInvalid() throws Exception {
            mockMvc.perform(get(API_URL)
                            .param("sort", "INVALID_SORT"))
                    .andExpect(status().isBadRequest());
            verify(getFinishedGamesUseCase, never()).execute(any());
        }

        @Test
        void shouldReturnBadRequestInvalidPlayerId() throws Exception {
            Long invalidPlayerId = -1L;

            mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                            .param("sort", "CREATED_AT_DESC")
                            .param("playerId", String.valueOf(invalidPlayerId)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", containsString("Validation Error in Parameter")))
                    .andExpect(jsonPath("$.errors", hasEntry("getGames.playerId", "must be greater than 0")))
                    .andExpect(jsonPath("$.errors", hasKey("getGames.playerId")));
            verify(getFinishedGamesUseCase, never()).execute(any());
        }
        @Test
        void shouldReturnBadRequestInvalidPlayerName() throws Exception {
            String invalidPlayerName = "a".repeat(31);

            mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                            .param("sort", "CREATED_AT_DESC")
                            .param("playerName", String.valueOf(invalidPlayerName)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", containsString("Validation Error in Parameter")))
                    .andExpect(jsonPath("$.errors", hasEntry("getGames.playerName", "size must be between 1 and 30")))
                    .andExpect(jsonPath("$.errors", hasKey("getGames.playerName")));
            verify(getFinishedGamesUseCase, never()).execute(any());
        }
    }
}