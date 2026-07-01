package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.player_profiles;
import cat.itacademy.blackjack.demo.common.infrastructure.web.handler.GlobalExceptionHandler;
import cat.itacademy.blackjack.demo.finished_game.application.exception.PlayerProfileNotFoundException;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfileUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfilesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.PlayerProfileSortType;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.player_profiles.dto.PlayerProfileResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerProfilesRestController.class)
@Import(GlobalExceptionHandler.class)
class PlayerProfilesRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetPlayerProfilesSortedUseCase getPlayerProfilesUseCase;

    @MockitoBean
    private GetPlayerProfileUseCase getPlayerProfileUseCase;

    @Test
    @DisplayName("GET /api/player-profiles/{id} - Success")
    void getPlayerById_ReturnsPlayer_WhenIdIsValid() throws Exception {
        Long playerId = 1L;
        PlayerProfileResponseDto response = new PlayerProfileResponseDto(playerId, "Player1", 10L, 5L, 100L);
        when(getPlayerProfileUseCase.getPlayerById(playerId)).thenReturn(response);

        mockMvc.perform(get("/api/player-profiles/{id}", playerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(playerId))
                .andExpect(jsonPath("$.name").value("Player1"));

        verify(getPlayerProfileUseCase).getPlayerById(playerId);

    }

    @Test
    @DisplayName("GET /api/player-profiles/{id} - Not Found (404)")
    void getPlayerById_Returns404_WhenPlayerDoesNotExist() throws Exception {
        Long playerId = 99L;
        when(getPlayerProfileUseCase.getPlayerById(playerId))
                .thenThrow(new PlayerProfileNotFoundException("Player not found with id: " + playerId));

        mockMvc.perform(get("/api/player-profiles/{id}", playerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found Error"))
                .andExpect(jsonPath("$.detail", containsString("Player not found with id")))
                .andExpect(jsonPath("$.detail", containsString("99")));

        verify(getPlayerProfileUseCase).getPlayerById(playerId);
    }

    @Test
    @DisplayName("GET /api/player-profiles/{id} - Bad Request (400) due to @Positive")
    void getPlayerById_Returns400_WhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/api/player-profiles/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error in Parameter"));
        verify(getPlayerProfileUseCase, never()).getPlayerById(any(Long.class));
    }

    @Test
    @DisplayName("GET /api/player-profiles/search - Success")
    void getPlayerByName_ReturnsPlayer_WhenNameExists() throws Exception {
        String name = "Pepe";
        PlayerProfileResponseDto response = new PlayerProfileResponseDto(1L, name, 5L, 2L, 50L);
        when(getPlayerProfileUseCase.getPlayerByName(name)).thenReturn(response);

        mockMvc.perform(get("/api/player-profiles/search").param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));

        verify(getPlayerProfileUseCase).getPlayerByName(name);
    }

    @Test
    @DisplayName("GET /api/player-profiles/search - Validation Error (400) Name blank")
    void getPlayerByName_Returns400_WhenNameIsBlank() throws Exception {
        mockMvc.perform(get("/api/player-profiles/search").param("name", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error in Parameter"));
        verify(getPlayerProfileUseCase, never()).getPlayerByName(any(String.class));
    }

    @Test
    @DisplayName("GET /api/player-profiles/search - Validation Error (400) Name exceeds max size")
    void getPlayerByName_Returns400_WhenNameExceedsMaxSize() throws Exception {
        String longName = "a".repeat(31);
        mockMvc.perform(get("/api/player-profiles/search").param("name", longName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error in Parameter"));
        verify(getPlayerProfileUseCase, never()).getPlayerByName(any(String.class));
    }

    @Test
    @DisplayName("GET /api/player-profiles - Success List")
    void getPlayers_ReturnsList_WithDefaultSort() throws Exception {
        PlayerProfileResponseDto p1 = new PlayerProfileResponseDto(1L, "A", 1L, 1L, 10L);
        PlayerProfileResponseDto p2 = new PlayerProfileResponseDto(2L, "B", 1L, 0L, 5L);
        when(getPlayerProfilesUseCase.execute(any(PlayerProfileSortType.class))).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/player-profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].name").value("B"));

        verify(getPlayerProfilesUseCase).execute(any(PlayerProfileSortType.class));
    }

    @Test
    @DisplayName("GET /api/player-profiles?sort=NUMBER_OF_GAMES_WON_DESC - Success")
    void getPlayers_ReturnsSortedList_WhenSortParamIsProvided() throws Exception {
        PlayerProfileSortType sortType = PlayerProfileSortType.NUMBER_OF_GAMES_WON_DESC;

        PlayerProfileResponseDto p1 = new PlayerProfileResponseDto(1L, "Winner", 10L, 9L, 500L);
        when(getPlayerProfilesUseCase.execute(sortType)).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/player-profiles")
                        .param("sort", "NUMBER_OF_GAMES_WON_DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Winner"));

        verify(getPlayerProfilesUseCase).execute(sortType);
    }

    @Test
    @DisplayName("GET /api/player-profiles?sort=INVALID_VALUE - Failure (400)")
    void getPlayers_Returns400_WhenSortValueIsInvalid() throws Exception {
        mockMvc.perform(get("/api/player-profiles")
                        .param("sort", "WHATEVER"))
                .andExpect(status().isBadRequest());

        verify(getPlayerProfilesUseCase, never()).execute(any(PlayerProfileSortType.class));
    }

    @Test
    @DisplayName("GET /api/player-profiles - Uses Default Value")
    void getPlayers_UsesDefaultValue_WhenNoParamIsProvided() throws Exception {
        PlayerProfileSortType defaultSort = PlayerProfileSortType.SCORE_DESC;
        when(getPlayerProfilesUseCase.execute(defaultSort)).thenReturn(List.of());

        mockMvc.perform(get("/api/player-profiles"))
                .andExpect(status().isOk());

        verify(getPlayerProfilesUseCase).execute(defaultSort);
    }
}