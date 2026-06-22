import cat.itacademy.blackjack.demo.BlackjackApplication;
import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.game.domain.GameState;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.CreateGameDto;
import cat.itacademy.blackjack.demo.shuffle_strategy.*;
import org.hamcrest.Matchers;
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
                        .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                        .andExpect(jsonPath("$.playerHand", hasSize(2)))
                        .andExpect(jsonPath("$.dealerFirstCard").exists())
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
                        .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                        .andExpect(jsonPath("$.playerHand", hasSize(2)))
                        .andExpect(jsonPath("$.dealerFirstCard").exists())
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
                        .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                        .andExpect(jsonPath("$.playerHand", hasSize(2)))
                        .andExpect(jsonPath("$.dealerFirstCard").exists())
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
    @Nested
    @DisplayName("GET " + BASE_API + "/{id}")
    class GetActiveGameState {

        @DisplayName("should return 200 with information about the state of the game")
        @Test
        void shouldGetInforAboutTheStateOfTheGameCreated() throws Exception {
            CreateGameDto createGameDto = new CreateGameDto(NAME);

            ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createGameDto)));

            String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
            String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_API + "/" + idFromJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createGameDto)));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(idFromJson))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                    .andExpect(jsonPath("$.username").value(NAME))
                    .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                    .andExpect(jsonPath("$.playerHand", hasSize(2)))
                    .andExpect(jsonPath("$.dealerFirstCard").exists());
        }

        @DisplayName("should return 404 Game Not Found when the game requested does not exist")
        @Test
        void shouldReturn404NotFound() throws Exception {
            String idStr = GameId.generate().toString();

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_API + "/" + idStr)
                    .contentType(MediaType.APPLICATION_JSON));
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", containsString("Game Not Found")))
                    .andExpect(jsonPath("$.detail", containsString("games")))
                    .andExpect(jsonPath("$.detail", containsString("found")))
                    .andExpect(jsonPath("$.detail", containsString("id")))
                    .andExpect(jsonPath("$.detail", containsString(idStr)));
        }
    }
        @Nested
        @DisplayName("POST " + BASE_API + "/{id}/hit")
        class Hit {

            @Test
            void shouldReturn400ValidationErrorInvalidId() throws Exception {
                String invalidId = "invalid-id";

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + invalidId + "/hit")
                        .contentType(MediaType.APPLICATION_JSON));

                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.title", Matchers.containsString("Validation Error in Parameter")));

            }

            @Nested
            @Import(GameWithoutBlackJackStrategyConfig.class)
            class GameIsNotFinishedAfterHit{

                @Autowired
                private MockMvc mockMvc;

                @Test
                void shouldReturnActiveGameInfoData() throws Exception {

                    CreateGameDto createGameDto = new CreateGameDto(NAME);

                    ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createGameDto)));

                    String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                    String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + idFromJson + "/hit")
                            .contentType(MediaType.APPLICATION_JSON));

                    result.andExpect(status().isOk())
                            .andExpect(jsonPath("$.id").value(idFromJson))
                            .andExpect(jsonPath("$.createdAt").exists())
                            .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                            .andExpect(jsonPath("$.username").value(NAME))
                            .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                            .andExpect(jsonPath("$.playerHand", hasSize(3)))
                            .andExpect(jsonPath("$.dealerFirstCard").exists())
                            .andExpect(jsonPath("$.gameState").value(GameState.STARTED.name()))
                            .andExpect(jsonPath("$.gameResult", anyOf(is(nullValue()))))
                            .andExpect(jsonPath("$.finishedWithBlackjack", anyOf(is(nullValue()))));

                }
            }

            @Nested
            @Import(PlayerLosingByExceeding21StrategyConfig.class)
            class GameIsFinishedAfterHitBecausePlayerExceeded21{

                @Autowired
                private MockMvc mockMvc;

                @Test
                void shouldReturnActiveGameInfoData() throws Exception {

                    CreateGameDto createGameDto = new CreateGameDto(NAME);

                    ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createGameDto)));

                    String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                    String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + idFromJson + "/hit")
                            .contentType(MediaType.APPLICATION_JSON));

                    result.andExpect(status().isOk())
                            .andExpect(jsonPath("$.id").value(idFromJson))
                            .andExpect(jsonPath("$.createdAt").exists())
                            .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                            .andExpect(jsonPath("$.username").value(NAME))
                            .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                            .andExpect(jsonPath("$.playerHand", hasSize(3)))
                            .andExpect(jsonPath("$.dealerFirstCard").exists())
                            .andExpect(jsonPath("$.gameState").value(GameState.OVER.name()))
                            .andExpect(jsonPath("$.gameResult").value(GameResult.DEALER_WIN.name()))
                            .andExpect(jsonPath("$.finishedWithBlackjack").value(false));

                }
            }

            @DisplayName("should return 404 Game Not Found when the service throws a GameNotFoundException")
            @Test
            void shouldReturn404NotFound() throws Exception {

                String idStr = GameId.generate().toString();

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + idStr + "/hit")
                        .contentType(MediaType.APPLICATION_JSON));
                result.andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.detail", containsString("Game")))
                        .andExpect(jsonPath("$.detail", containsString("id")))
                        .andExpect(jsonPath("$.detail", containsString(idStr)))
                        .andExpect(jsonPath("$.detail", containsString("found")));
            }
        }

    @Nested
    @DisplayName("POST " + BASE_API + "/{id}/stand")
    class Stand {

        @Test
        void shouldReturn400ValidationErrorInvalidId() throws Exception {
            String invalidId = "invalid-id";

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + invalidId + "/stand")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", Matchers.containsString("Validation Error in Parameter")));

        }

        @DisplayName("should return 404 Game Not Found when the service throws a GameNotFoundException")
        @Test
        void shouldReturn404NotFound() throws Exception {
            String generatedId = GameId.generate().toString();

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + generatedId + "/stand")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", containsString("Game")))
                    .andExpect(jsonPath("$.detail", containsString("not")))
                    .andExpect(jsonPath("$.detail", containsString("found")))
                    .andExpect(jsonPath("$.detail", containsString(generatedId)));
        }

        @Nested
        @Import(DealerWinningWithBlackJackStrategyConfig.class)
        class DealerWinningWithBlackJackStrategyGame {

            @Autowired
            private MockMvc mockMvc;

            @Test
            void shouldReturnActiveGameInfoData() throws Exception {
                CreateGameDto createGameDto = new CreateGameDto(NAME);

                ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGameDto)));

                String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + idFromJson + "/stand")
                        .contentType(MediaType.APPLICATION_JSON));

                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(idFromJson))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                        .andExpect(jsonPath("$.username").value(NAME))
                        .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                        .andExpect(jsonPath("$.playerHand", hasSize(2)))
                        .andExpect(jsonPath("$.dealerFirstCard").exists())
                        .andExpect(jsonPath("$.dealerTotalCardsValue").value(21))
                        .andExpect(jsonPath("$.gameState").value("OVER"))
                        .andExpect(jsonPath("$.gameResult").value(GameResult.DEALER_WIN.name()))
                        .andExpect(jsonPath("$.finishedWithBlackjack").value(true))
                        .andExpect(jsonPath("$.dealerFinalHand", hasSize(2)));
            }
        }
        @Nested
        @Import(PlayerWinningByCardsValueStrategyConfig.class)
        class PlayerWinningByCardsValueStrategyGame {

            @Autowired
            private MockMvc mockMvc;

            @Test
            void shouldReturnActiveGameInfoData() throws Exception {
                CreateGameDto createGameDto = new CreateGameDto(NAME);

                ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGameDto)));

                String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + idFromJson + "/stand")
                        .contentType(MediaType.APPLICATION_JSON));

                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(idFromJson))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                        .andExpect(jsonPath("$.username").value(NAME))
                        .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                        .andExpect(jsonPath("$.playerHand", hasSize(2)))
                        .andExpect(jsonPath("$.dealerFirstCard").exists())
                        .andExpect(jsonPath("$.dealerTotalCardsValue").exists())
                        .andExpect(jsonPath("$.gameState").value("OVER"))
                        .andExpect(jsonPath("$.gameResult").value(GameResult.USER_WIN.name()))
                        .andExpect(jsonPath("$.finishedWithBlackjack").value(false))
                        .andExpect(jsonPath("$.dealerFinalHand", hasSize(2)));
            }
        }

        @Nested
        @Import(DealerWinningByCardsValueStrategyConfig.class)
        class DealerWinningByCardsValueStrategyGame {

            @Autowired
            private MockMvc mockMvc;

            @Test
            void shouldReturnActiveGameInfoData() throws Exception {
                CreateGameDto createGameDto = new CreateGameDto(NAME);

                ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGameDto)));

                String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + idFromJson + "/stand")
                        .contentType(MediaType.APPLICATION_JSON));

                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(idFromJson))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                        .andExpect(jsonPath("$.username").value(NAME))
                        .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                        .andExpect(jsonPath("$.playerHand", hasSize(2)))
                        .andExpect(jsonPath("$.dealerFirstCard").exists())
                        .andExpect(jsonPath("$.dealerTotalCardsValue").exists())
                        .andExpect(jsonPath("$.gameState").value("OVER"))
                        .andExpect(jsonPath("$.gameResult").value(GameResult.DEALER_WIN.name()))
                        .andExpect(jsonPath("$.finishedWithBlackjack").value(false))
                        .andExpect(jsonPath("$.dealerFinalHand", hasSize(2)));
            }
        }
        @Nested
        @Import(TieWithoutBlackJackStrategyConfig.class)
        class TieWithoutBlackJackStrategyGame {

            @Autowired
            private MockMvc mockMvc;

            @Test
            void shouldReturnActiveGameInfoData() throws Exception {
                CreateGameDto createGameDto = new CreateGameDto(NAME);

                ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGameDto)));

                String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + idFromJson + "/stand")
                        .contentType(MediaType.APPLICATION_JSON));

                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(idFromJson))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                        .andExpect(jsonPath("$.username").value(NAME))
                        .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                        .andExpect(jsonPath("$.playerHand", hasSize(2)))
                        .andExpect(jsonPath("$.dealerFirstCard").exists())
                        .andExpect(jsonPath("$.dealerTotalCardsValue").exists())
                        .andExpect(jsonPath("$.gameState").value("OVER"))
                        .andExpect(jsonPath("$.gameResult").value(GameResult.TIE.name()))
                        .andExpect(jsonPath("$.finishedWithBlackjack").value(false))
                        .andExpect(jsonPath("$.dealerFinalHand", hasSize(2)));
            }
        }

        @Nested
        @Import(DealerLosingByExceeding21StrategyConfig.class)
        class DealerLosingByExceeding21StrategyGame {

            @Autowired
            private MockMvc mockMvc;

            @Test
            void shouldReturnActiveGameInfoData() throws Exception {
                CreateGameDto createGameDto = new CreateGameDto(NAME);

                ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGameDto)));

                String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API + "/" + idFromJson + "/stand")
                        .contentType(MediaType.APPLICATION_JSON));

                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(idFromJson))
                        .andExpect(jsonPath("$.createdAt").exists())
                        .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                        .andExpect(jsonPath("$.username").value(NAME))
                        .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                        .andExpect(jsonPath("$.playerHand", hasSize(2)))
                        .andExpect(jsonPath("$.dealerFirstCard").exists())
                        .andExpect(jsonPath("$.dealerTotalCardsValue").exists())
                        .andExpect(jsonPath("$.gameState").value("OVER"))
                        .andExpect(jsonPath("$.gameResult").value(GameResult.USER_WIN.name()))
                        .andExpect(jsonPath("$.finishedWithBlackjack").value(false))
                        .andExpect(jsonPath("$.dealerFinalHand", hasSize(3)));
            }
        }
    }
    @Nested
    @DisplayName("GET " + BASE_API)
    class GetAllActiveGames {

        @Test
        void shouldReturn200WithListOfCreatedGames() throws Exception {
            CreateGameDto createGameDto1 = new CreateGameDto(NAME);

            mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createGameDto1)));

            CreateGameDto createGameDto2 = new CreateGameDto("Another Name");

            mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createGameDto2)));

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_API)
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[1].id").exists());
        }
    }
}