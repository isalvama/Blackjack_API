import cat.itacademy.blackjack.demo.BlackjackApplication;
import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.CreateGameDto;
import cat.itacademy.blackjack.demo.shuffle_strategy.GameWithoutBlackJackStrategyConfig;
import cat.itacademy.blackjack.demo.shuffle_strategy.TieWithBlackjackShuffleStrategyConfig;
import cat.itacademy.blackjack.demo.shuffle_strategy.UserWinningWithBlackjackStrategyConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BlackjackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mongodb")
@Testcontainers
@EnableJpaRepositories(basePackages = "cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.springDataRepository")
@AutoConfigureMockMvc
public class BlackjackIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0.36");

    @Container
    @ServiceConnection
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:7.0");

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_API = "/api/blackjack";
    private static final String NAME = "Test Name";

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST " + BASE_API)
    class StartGame {

        @Nested
        @Import(GameWithoutBlackJackStrategyConfig.class)
        class GameStartedWithoutBlackJack {

            @Autowired
            private MockMvc mockMvc;

            @DisplayName("should return 201 with information about the game as started")
            @Test
            void shouldStartGame() throws Exception {
                CreateGameDto createGameDto = new CreateGameDto(NAME);

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGameDto)));

                result.andExpect(status().isCreated())
                        .andExpect(header().string("Location", containsString(BASE_API + "/")))
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                        .andExpect(jsonPath("$.username").value(NAME))
                        .andExpect(jsonPath("$.totalCardsValue").isNumber())
                        .andExpect(jsonPath("$.hand", hasSize(2)))
                        .andExpect(jsonPath("$.gameState").value("STARTED"))
                        .andExpect(jsonPath("$.gameResult", anyOf(is(nullValue()))))
                        .andExpect(jsonPath("$.finishedWithBlackjack", anyOf(is(nullValue()))));

                String resultAsString = result.andReturn().getResponse().getContentAsString();
                String createdAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.createdAt");
                LocalDateTime createdAt = LocalDateTime.parse(createdAtFromEscapedJson);
                assertThat(createdAt).isAfter(LocalDateTime.now().minusMinutes(1));

                String lastTimePlayedAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.lastTimePlayedAt");
                LocalDateTime lastTimePlayedAt = LocalDateTime.parse(lastTimePlayedAtFromEscapedJson);
                assertThat(lastTimePlayedAt).isAfter(LocalDateTime.now().minusMinutes(1));

                assertThat(lastTimePlayedAt).isAfter(createdAt);
            }
        }

        @Nested
        @Import(UserWinningWithBlackjackStrategyConfig.class)
        class UserWinningWithBlackjackTestCase {

            @Autowired
            private MockMvc mockMvc;

            @DisplayName("should return 201 with information about the game as finished with the userPlayer winning with BlackJack")
            @Test
            void shouldFinishAsGameUserWinningWithBlackJack() throws Exception {

                CreateGameDto createGameDto = new CreateGameDto(NAME);

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGameDto)));

                result.andExpect(status().isCreated())
                        .andExpect(header().string("Location", containsString(BASE_API + "/")))
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                        .andExpect(jsonPath("$.username").value(NAME))
                        .andExpect(jsonPath("$.totalCardsValue").isNumber())
                        .andExpect(jsonPath("$.hand", hasSize(2)))
                        .andExpect(jsonPath("$.gameState").value("OVER"))
                        .andExpect(jsonPath("$.gameResult").value(GameResult.USER_WIN.name()))
                        .andExpect(jsonPath("$.finishedWithBlackjack").value("true"));

                String resultAsString = result.andReturn().getResponse().getContentAsString();
                String createdAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.createdAt");
                LocalDateTime createdAt = LocalDateTime.parse(createdAtFromEscapedJson);
                assertThat(createdAt).isAfter(LocalDateTime.now().minusMinutes(1));

                String lastTimePlayedAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.lastTimePlayedAt");
                LocalDateTime lastTimePlayedAt = LocalDateTime.parse(lastTimePlayedAtFromEscapedJson);
                assertThat(lastTimePlayedAt).isAfter(LocalDateTime.now().minusMinutes(1));

                assertThat(lastTimePlayedAt).isAfter(createdAt);
            }
        }

        @Nested
        @Import(TieWithBlackjackShuffleStrategyConfig.class)
        class TieWithBlackjackTestCase {

            @Autowired
            private MockMvc mockMvc;

            @DisplayName("should return 201 with information about the game as finished with tie and BlackJack")
            @Test
            void shouldFinishWithTieAndBlackJack() throws Exception {

                CreateGameDto createGameDto = new CreateGameDto(NAME);

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGameDto)));

                result.andExpect(status().isCreated())
                        .andExpect(header().string("Location", containsString(BASE_API + "/")))
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                        .andExpect(jsonPath("$.username").value(NAME))
                        .andExpect(jsonPath("$.totalCardsValue").isNumber())
                        .andExpect(jsonPath("$.hand", hasSize(2)))
                        .andExpect(jsonPath("$.gameState").value("OVER"))
                        .andExpect(jsonPath("$.gameResult").value(GameResult.TIE.name()))
                        .andExpect(jsonPath("$.finishedWithBlackjack").value("true"));
                String resultAsString = result.andReturn().getResponse().getContentAsString();
                String createdAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.createdAt");
                LocalDateTime createdAt = LocalDateTime.parse(createdAtFromEscapedJson);
                assertThat(createdAt).isAfter(LocalDateTime.now().minusMinutes(1));

                String lastTimePlayedAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.lastTimePlayedAt");
                LocalDateTime lastTimePlayedAt = LocalDateTime.parse(lastTimePlayedAtFromEscapedJson);
                assertThat(lastTimePlayedAt).isAfter(LocalDateTime.now().minusMinutes(1));

                assertThat(lastTimePlayedAt).isAfter(createdAt);
            }
        }
    }
}