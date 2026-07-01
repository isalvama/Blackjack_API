import cat.itacademy.blackjack.demo.BlackjackApplication;
import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.active_game.domain.GameState;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.CreateGameDto;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaFinishedGameEntity;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaPlayerProfileEntity;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository.JpaFinishedGameSpringDataRepository;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository.JpaPlayerProfileSpringDataRepository;
import cat.itacademy.blackjack.demo.shuffle_strategy.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BlackjackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mongodb")
@Testcontainers
@EnableJpaRepositories(basePackages = "cat.itacademy.blackjack.demo")
@EnableMongoRepositories(basePackages = "cat.itacademy.blackjack.demo")
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:tc:mysql:8.0.36:///test"
})
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

    @Autowired
    private JpaFinishedGameSpringDataRepository finishedGameRepository;

    @Autowired
    private JpaPlayerProfileSpringDataRepository playerProfileRepository;

    private static final String API_ACTIVE_GAMES = "/api/active-games";
    private static final String API_PLAYER_PROFILES = "/api/player-profiles";
    private static final String API_FINISHED_GAMES = "/api/finished-games";
    private static final String NAME = "Test Name";

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName(API_ACTIVE_GAMES)
    class ActiveGame {

        @Nested
        @DisplayName("POST " + API_ACTIVE_GAMES)
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

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createGameDto)));

                    result.andExpect(status().isCreated())
                            .andExpect(header().string("Location", containsString(API_ACTIVE_GAMES + "/")))
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

                    String idFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                    await().atMost(5, SECONDS).untilAsserted(() -> {
                        mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromEscapedJson)
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
                    });
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

                    ResultActions resultCreateGame = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createGameDto)));

                    resultCreateGame.andExpect(status().isCreated())
                            .andExpect(header().string("Location", containsString(API_ACTIVE_GAMES + "/")))
                            .andExpect(jsonPath("$.id").exists())
                            .andExpect(jsonPath("$.createdAt").exists())
                            .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                            .andExpect(jsonPath("$.username").value(NAME))
                            .andExpect(jsonPath("$.playerTotalCardsValue").value(21))
                            .andExpect(jsonPath("$.playerHand", hasSize(2)))
                            .andExpect(jsonPath("$.dealerFirstCard").exists())
                            .andExpect(jsonPath("$.gameState").value("OVER"))
                            .andExpect(jsonPath("$.gameResult").value(GameResult.USER_WIN.name()))
                            .andExpect(jsonPath("$.finishedWithBlackjack").value("true"))
                            .andExpect(jsonPath("$.dealerFinalHand", hasSize(2)));


                    String resultAsString = resultCreateGame.andReturn().getResponse().getContentAsString();
                    String createdAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.createdAt");
                    LocalDateTime createdAt = LocalDateTime.parse(createdAtFromEscapedJson);
                    assertThat(createdAt).isAfter(LocalDateTime.now().minusMinutes(1));

                    String lastTimePlayedAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.lastTimePlayedAt");
                    LocalDateTime lastTimePlayedAt = LocalDateTime.parse(lastTimePlayedAtFromEscapedJson);
                    assertThat(lastTimePlayedAt).isAfter(LocalDateTime.now().minusMinutes(1));

                    assertThat(lastTimePlayedAt).isAfter(createdAt);

                    String idFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                    await().atMost(5, SECONDS).untilAsserted(() -> {
                        mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromEscapedJson)
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.gameId").value(idFromEscapedJson))
                                .andExpect(jsonPath("$.playerName").value(NAME))
                                .andExpect(jsonPath("$.playerNumberOfCards").value(2))
                                .andExpect(jsonPath("$.dealerNumberOfCards").value(2))
                                .andExpect(jsonPath("$.gameResult").value(GameResult.USER_WIN.name()))
                                .andExpect(jsonPath("$.finishedWithBlackjack").value("true"))
                                .andExpect(jsonPath("$.totalCardsValuePlayer").value(21))
                                .andExpect(jsonPath("$.score").value(21));
                    });
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

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createGameDto)));

                    result.andExpect(status().isCreated())
                            .andExpect(header().string("Location", containsString(API_ACTIVE_GAMES + "/")))
                            .andExpect(jsonPath("$.id").exists())
                            .andExpect(jsonPath("$.createdAt").exists())
                            .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                            .andExpect(jsonPath("$.username").value(NAME))
                            .andExpect(jsonPath("$.playerTotalCardsValue").value(21))
                            .andExpect(jsonPath("$.playerHand", hasSize(2)))
                            .andExpect(jsonPath("$.dealerFirstCard").exists())
                            .andExpect(jsonPath("$.gameState").value("OVER"))
                            .andExpect(jsonPath("$.gameResult").value(GameResult.TIE.name()))
                            .andExpect(jsonPath("$.finishedWithBlackjack").value("true"))
                            .andExpect(jsonPath("$.dealerFinalHand", hasSize(2)))
                            .andExpect(jsonPath("$.dealerTotalCardsValue").value(21));


                    String resultAsString = result.andReturn().getResponse().getContentAsString();
                    String createdAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.createdAt");
                    LocalDateTime createdAt = LocalDateTime.parse(createdAtFromEscapedJson);
                    assertThat(createdAt).isAfter(LocalDateTime.now().minusMinutes(1));

                    String lastTimePlayedAtFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.lastTimePlayedAt");
                    LocalDateTime lastTimePlayedAt = LocalDateTime.parse(lastTimePlayedAtFromEscapedJson);
                    assertThat(lastTimePlayedAt).isAfter(LocalDateTime.now().minusMinutes(1));

                    assertThat(lastTimePlayedAt).isAfter(createdAt);

                    String idFromEscapedJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                    await().atMost(5, SECONDS).untilAsserted(() -> {
                        mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromEscapedJson)
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.gameId").value(idFromEscapedJson))
                                .andExpect(jsonPath("$.playerName").value(NAME))
                                .andExpect(jsonPath("$.playerNumberOfCards").value(2))
                                .andExpect(jsonPath("$.dealerNumberOfCards").value(2))
                                .andExpect(jsonPath("$.gameResult").value(GameResult.TIE.name()))
                                .andExpect(jsonPath("$.finishedWithBlackjack").value("true"))
                                .andExpect(jsonPath("$.totalCardsValuePlayer").value(21))
                                .andExpect(jsonPath("$.totalCardsValueDealer").value(21))
                                .andExpect(jsonPath("$.score").value(11));
                    });
                }
            }

            @Nested
            @DisplayName("GET " + API_ACTIVE_GAMES + "/{id}")
            class GetActiveGameState {
                @Nested
                @Import(GameWithoutBlackJackStrategyConfig.class)
                class GameStartedAndNotFinished {

                    @Autowired
                    private MockMvc mockMvc;

                    @DisplayName("should return 200 with information about the state of the game")
                    @Test
                    void shouldGetInfoAboutTheStateOfTheGameCreated() throws Exception {
                        CreateGameDto createGameDto = new CreateGameDto(NAME);

                        ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createGameDto)));

                        String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                        String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(API_ACTIVE_GAMES + "/" + idFromJson)
                                .contentType(MediaType.APPLICATION_JSON));

                        result.andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(idFromJson))
                                .andExpect(jsonPath("$.createdAt").exists())
                                .andExpect(jsonPath("$.lastTimePlayedAt").exists())
                                .andExpect(jsonPath("$.username").value(NAME))
                                .andExpect(jsonPath("$.playerTotalCardsValue").isNumber())
                                .andExpect(jsonPath("$.playerHand", hasSize(2)))
                                .andExpect(jsonPath("$.dealerFirstCard").exists());

                        await().atMost(5, SECONDS).untilAsserted(() -> {
                            mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromJson)
                                            .contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isNotFound());
                        });
                    }
                }

                @DisplayName("should return 404 Game Not Found when the game requested does not exist")
                @Test
                void shouldReturn404NotFound() throws Exception {
                    String idStr = GameId.generate().toString();

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(API_ACTIVE_GAMES + "/" + idStr)
                            .contentType(MediaType.APPLICATION_JSON));
                    result.andExpect(status().isNotFound())
                            .andExpect(jsonPath("$.detail", containsString("Game Not Found")))
                            .andExpect(jsonPath("$.detail", containsString("games")))
                            .andExpect(jsonPath("$.detail", containsString("found")))
                            .andExpect(jsonPath("$.detail", containsString("id")))
                            .andExpect(jsonPath("$.detail", containsString(idStr)));
                }
            }
        }

            @Nested
            @DisplayName("POST " + API_ACTIVE_GAMES + "/{id}/hit")
            class Hit {

                @Test
                void shouldReturn400ValidationErrorInvalidId() throws Exception {
                    String invalidId = "invalid-id";

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + invalidId + "/hit")
                            .contentType(MediaType.APPLICATION_JSON));

                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.title", Matchers.containsString("Validation Error in Parameter")));

                }

                @Nested
                @Import(GameWithoutBlackJackStrategyConfig.class)
                class GameIsNotFinishedAfterHit {

                    @Autowired
                    private MockMvc mockMvc;

                    @Test
                    void shouldReturnActiveGameInfoData() throws Exception {

                        CreateGameDto createGameDto = new CreateGameDto(NAME);

                        ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createGameDto)));

                        String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                        String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + idFromJson + "/hit")
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

                        await().atMost(5, SECONDS).untilAsserted(() -> {
                            mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromJson)
                                            .contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isNotFound());
                        });
                    }
                }

                @Nested
                @Import(PlayerLosingByExceeding21StrategyConfig.class)
                class GameIsFinishedAfterHitBecausePlayerExceeded21 {

                    @Autowired
                    private MockMvc mockMvc;

                    @Test
                    void shouldReturnActiveGameInfoData() throws Exception {

                        CreateGameDto createGameDto = new CreateGameDto(NAME);

                        ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createGameDto)));

                        String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                        String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + idFromJson + "/hit")
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

                        await().atMost(5, SECONDS).untilAsserted(() -> {
                            mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromJson)
                                            .contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(jsonPath("$.gameId").value(idFromJson))
                                    .andExpect(jsonPath("$.playerName").value(NAME))
                                    .andExpect(jsonPath("$.playerNumberOfCards").value(3))
                                    .andExpect(jsonPath("$.gameResult").value(GameResult.DEALER_WIN.name()))
                                    .andExpect(jsonPath("$.finishedWithBlackjack").value("false"))
                                    .andExpect(jsonPath("$.score").value(0));
                        });
                    }
                }

                @DisplayName("should return 404 Game Not Found when the service throws a GameNotFoundException")
                @Test
                void shouldReturn404NotFound() throws Exception {

                    String idStr = GameId.generate().toString();

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + idStr + "/hit")
                            .contentType(MediaType.APPLICATION_JSON));
                    result.andExpect(status().isNotFound())
                            .andExpect(jsonPath("$.detail", containsString("Game")))
                            .andExpect(jsonPath("$.detail", containsString("id")))
                            .andExpect(jsonPath("$.detail", containsString(idStr)))
                            .andExpect(jsonPath("$.detail", containsString("found")));
                }
            }

            @Nested
            @DisplayName("POST " + API_ACTIVE_GAMES + "/{id}/stand")
            class Stand {

                @Test
                void shouldReturn400ValidationErrorInvalidId() throws Exception {
                    String invalidId = "invalid-id";

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + invalidId + "/stand")
                            .contentType(MediaType.APPLICATION_JSON));

                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.title", Matchers.containsString("Validation Error in Parameter")));

                }

                @DisplayName("should return 404 Game Not Found when the service throws a GameNotFoundException")
                @Test
                void shouldReturn404NotFound() throws Exception {
                    String generatedId = GameId.generate().toString();

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + generatedId + "/stand")
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

                        ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createGameDto)));

                        String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                        String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + idFromJson + "/stand")
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

                        await().atMost(5, SECONDS).untilAsserted(() -> {
                            mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromJson)
                                            .contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(jsonPath("$.gameId").value(idFromJson))
                                    .andExpect(jsonPath("$.playerName").value(NAME))
                                    .andExpect(jsonPath("$.playerNumberOfCards").value(2))
                                    .andExpect(jsonPath("$.dealerNumberOfCards").value(2))
                                    .andExpect(jsonPath("$.gameResult").value(GameResult.DEALER_WIN.name()))
                                    .andExpect(jsonPath("$.finishedWithBlackjack").value("true"))
                                    .andExpect(jsonPath("$.totalCardsValueDealer").value(21))
                                    .andExpect(jsonPath("$.score").value(0));
                        });
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

                        ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createGameDto)));

                        String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                        String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + idFromJson + "/stand")
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

                        await().atMost(5, SECONDS).untilAsserted(() -> {
                            mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromJson)
                                            .contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(jsonPath("$.gameId").value(idFromJson))
                                    .andExpect(jsonPath("$.playerName").value(NAME))
                                    .andExpect(jsonPath("$.playerNumberOfCards").value(2))
                                    .andExpect(jsonPath("$.dealerNumberOfCards").value(2))
                                    .andExpect(jsonPath("$.gameResult").value(GameResult.USER_WIN.name()))
                                    .andExpect(jsonPath("$.finishedWithBlackjack").value("false"));
                        });
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

                        ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createGameDto)));

                        String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                        String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + idFromJson + "/stand")
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

                        await().atMost(5, SECONDS).untilAsserted(() -> {
                            mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromJson)
                                            .contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(jsonPath("$.gameId").value(idFromJson))
                                    .andExpect(jsonPath("$.playerName").value(NAME))
                                    .andExpect(jsonPath("$.playerNumberOfCards").value(2))
                                    .andExpect(jsonPath("$.dealerNumberOfCards").value(2))
                                    .andExpect(jsonPath("$.gameResult").value(GameResult.DEALER_WIN.name()))
                                    .andExpect(jsonPath("$.finishedWithBlackjack").value("false"));
                        });
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

                        ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createGameDto)));

                        String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                        String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + idFromJson + "/stand")
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

                        await().atMost(5, SECONDS).untilAsserted(() -> {
                            mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromJson)
                                            .contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(jsonPath("$.gameId").value(idFromJson))
                                    .andExpect(jsonPath("$.playerName").value(NAME))
                                    .andExpect(jsonPath("$.playerNumberOfCards").value(2))
                                    .andExpect(jsonPath("$.dealerNumberOfCards").value(2))
                                    .andExpect(jsonPath("$.gameResult").value(GameResult.TIE.name()))
                                    .andExpect(jsonPath("$.finishedWithBlackjack").value("false"));
                        });
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

                        ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createGameDto)));

                        String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                        String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES + "/" + idFromJson + "/stand")
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

                        await().atMost(5, SECONDS).untilAsserted(() -> {
                            mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + idFromJson)
                                            .contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isOk())
                                    .andExpect(jsonPath("$.gameId").value(idFromJson))
                                    .andExpect(jsonPath("$.playerName").value(NAME))
                                    .andExpect(jsonPath("$.playerNumberOfCards").value(2))
                                    .andExpect(jsonPath("$.dealerNumberOfCards").value(3))
                                    .andExpect(jsonPath("$.gameResult").value(GameResult.USER_WIN.name()))
                                    .andExpect(jsonPath("$.finishedWithBlackjack").value("false"));
                        });

                    }
                }
            }

            @Nested
            @DisplayName("GET " + API_ACTIVE_GAMES)
            class GetAllActiveGames {

                @Test
                void shouldReturn200WithListOfCreatedGames() throws Exception {
                    CreateGameDto createGameDto1 = new CreateGameDto(NAME);

                    mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createGameDto1)));

                    CreateGameDto createGameDto2 = new CreateGameDto("Another Name");

                    mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createGameDto2)));

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(API_ACTIVE_GAMES)
                            .contentType(MediaType.APPLICATION_JSON));

                    result.andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(2)))
                            .andExpect(jsonPath("$[0].id").exists())
                            .andExpect(jsonPath("$[1].id").exists());
                }

                @Test
                void shouldReturn200WithAnEmptyList() throws Exception {

                    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(API_ACTIVE_GAMES)
                            .contentType(MediaType.APPLICATION_JSON));

                    result.andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(0)))
                            .andExpect(jsonPath("$[0].id").doesNotExist())
                            .andExpect(jsonPath("$[1].id").doesNotExist());
                }
            }
            @Nested
            @DisplayName("DELETE " + API_ACTIVE_GAMES)
            class DeleteGame {

                @Nested
                @Import(GameWithoutBlackJackStrategyConfig.class)
                class GameStartedWithoutBlackJack {

                    @Autowired
                    private MockMvc mockMvc;

                    @DisplayName("should delete game started")
                    @Test
                    void shouldDeleteGameStarted() throws Exception {
                        CreateGameDto createGameDto = new CreateGameDto(NAME);

                        ResultActions resultCreate = mockMvc.perform(MockMvcRequestBuilders.post(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createGameDto)))
                                .andExpect(status().isCreated())
                                .andExpect(header().string("Location", containsString(API_ACTIVE_GAMES + "/")))
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.username").value(NAME))
                                .andExpect(jsonPath("$.gameState").value("STARTED"))
                                .andExpect(jsonPath("$.gameResult", anyOf(is(nullValue()))))
                                .andExpect(jsonPath("$.finishedWithBlackjack", anyOf(is(nullValue()))));

                        String resultAsString = resultCreate.andReturn().getResponse().getContentAsString();
                        String idFromJson = com.jayway.jsonpath.JsonPath.read(resultAsString, "$.id");

                        ResultActions resultBeforeDelete = mockMvc.perform(MockMvcRequestBuilders.get(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON));

                        resultBeforeDelete.andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].id").value(idFromJson))
                                .andExpect(jsonPath("$[0].username").value(NAME));

                        mockMvc.perform(MockMvcRequestBuilders.delete(API_ACTIVE_GAMES + "/" + idFromJson)
                                .contentType(MediaType.APPLICATION_JSON));


                        ResultActions resultAfterDelete = mockMvc.perform(MockMvcRequestBuilders.get(API_ACTIVE_GAMES)
                                .contentType(MediaType.APPLICATION_JSON));

                        resultAfterDelete.andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(0)))
                                .andExpect(jsonPath("$[0].id").doesNotExist());

                    }
                }
            }
        }

        @Nested
        @DisplayName(API_PLAYER_PROFILES)
        class PlayerProfile {

            @BeforeEach
            void setUp() {
                playerProfileRepository.deleteAll();
                playerProfileRepository.save(new JpaPlayerProfileEntity(null, "Alice", 10L, 6L, 150L));
                playerProfileRepository.save(new JpaPlayerProfileEntity(null, "Bob", 20L, 2L, 50L));
                playerProfileRepository.save(new JpaPlayerProfileEntity(null, "Charlie", 5L, 5L, 200L));
                playerProfileRepository.save(new JpaPlayerProfileEntity(null, "Ana", 12L, 10L, 400L));
                playerProfileRepository.save(new JpaPlayerProfileEntity(null, "Joe", 14L, 4L, 100L));

            }

            @Nested
            @DisplayName("GET " + API_PLAYER_PROFILES + "/{id}")
            class GetPlayerById {

                @Test
                void shouldReturnPlayerProfileInfoFromDatabase() throws Exception {
                    Long existingId = playerProfileRepository.findAll().getFirst().getId();

                    mockMvc.perform(MockMvcRequestBuilders.get(API_PLAYER_PROFILES + "/{id}", existingId))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.id").value(existingId))
                            .andExpect(jsonPath("$.name").exists());
                }

                @Test
                void shouldReturn404PlayerProfileNotFoundError() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_PLAYER_PROFILES + "/{id}", 9999L))
                            .andExpect(status().isNotFound())
                            .andExpect(jsonPath("$.title").value("Player Profile Not Found Error"))
                            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
                }
            }

            @Nested
            @DisplayName("GET " + API_PLAYER_PROFILES + "/search")
            class GetPlayerByName {

                @Test
                @DisplayName("Integration - Search Player by Name")
                void shouldReturnInfoOfPlayerWithNameFromDatabase() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_PLAYER_PROFILES + "/search")
                                    .param("name", "Alice"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.name").value("Alice"))
                            .andExpect(jsonPath("$.score").value(150));
                }

                @Test
                void shouldReturn404PlayerProfileNotFoundError() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_PLAYER_PROFILES + "/search")
                                    .param("name", "inexistant name"))
                            .andExpect(status().isNotFound())
                            .andExpect(jsonPath("$.title").value("Player Profile Not Found Error"))
                            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
                }
            }

            @Nested
            @DisplayName("GET " + API_PLAYER_PROFILES)
            class GetPlayers {

                @Test
                void shouldReturnAllPlayersSortedByScoreDesc() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_PLAYER_PROFILES))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(5)))
                            .andExpect(jsonPath("$[0].name").value("Ana"))
                            .andExpect(jsonPath("$[1].name").value("Charlie"))
                            .andExpect(jsonPath("$[2].name").value("Alice"))
                            .andExpect(jsonPath("$[3].name").value("Joe"))
                            .andExpect(jsonPath("$[4].name").value("Bob"));
                }

                @Test
                void shouldReturnAllPlayersSortedByGamesPlayedDesc() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_PLAYER_PROFILES)
                                    .param("sort", "NUMBER_OF_GAMES_PLAYED_DESC"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(5)))
                            .andExpect(jsonPath("$[0].name").value("Bob"))
                            .andExpect(jsonPath("$[1].name").value("Joe"))
                            .andExpect(jsonPath("$[2].name").value("Ana"))
                            .andExpect(jsonPath("$[3].name").value("Alice"))
                            .andExpect(jsonPath("$[4].name").value("Charlie"));
                }

                @Test
                void shouldReturnAllPlayersSortedByGamesWonDesc() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_PLAYER_PROFILES)
                                    .param("sort", "NUMBER_OF_GAMES_WON_DESC"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(5)))
                            .andExpect(jsonPath("$[0].name").value("Ana"))
                            .andExpect(jsonPath("$[1].name").value("Alice"))
                            .andExpect(jsonPath("$[2].name").value("Charlie"))
                            .andExpect(jsonPath("$[3].name").value("Joe"))
                            .andExpect(jsonPath("$[4].name").value("Bob"));
                }
            }
        }

        @Nested
        @DisplayName(API_FINISHED_GAMES)
        class FinishedGames {

            Long aliceId;
            Long bobId;
            JpaFinishedGameEntity aliceGame1;
            JpaFinishedGameEntity aliceGame2;
            JpaFinishedGameEntity aliceGame3;
            JpaFinishedGameEntity bobGame1;
            JpaFinishedGameEntity bobGame2;

            @BeforeEach
            void setUp() {
                finishedGameRepository.deleteAll();
                playerProfileRepository.deleteAll();

                JpaPlayerProfileEntity alice = playerProfileRepository.save(new JpaPlayerProfileEntity(null, "Alice", 0L, 0L, 0L));
                JpaPlayerProfileEntity bob = playerProfileRepository.save(new JpaPlayerProfileEntity(null, "Bob", 0L, 0L, 0L));
                aliceId = alice.getId();
                bobId = bob.getId();

                LocalDateTime now = LocalDateTime.now();
                aliceGame1 = saveCustomGame(alice, 100, now.minusHours(2), now.minusHours(2));
                aliceGame2 = saveCustomGame(alice, 900, now.minusHours(5), now.minusHours(4));
                aliceGame3 = saveCustomGame(alice, 500, now.minusHours(10), now);
                bobGame1 = saveCustomGame(bob, 700, now.minusHours(1), now.minusHours(1));
                bobGame2 = saveCustomGame(bob, 50, now.minusHours(8), now.minusHours(7));
            }

            private JpaFinishedGameEntity saveCustomGame(JpaPlayerProfileEntity player, Integer score, LocalDateTime createdAt, LocalDateTime finishedAt) {
                JpaFinishedGameEntity entity = new JpaFinishedGameEntity(
                        null, java.util.UUID.randomUUID(), player, 2, 2, 20, 20,
                        cat.itacademy.blackjack.demo.common.domain.GameResult.USER_WIN,
                        false, score, createdAt, finishedAt
                );
                return finishedGameRepository.save(entity);
            }

            @Nested
            @DisplayName(API_FINISHED_GAMES + "/{id}")
            class GetGameById {

                @Test
                void shouldReturnGameWithMatchingIdFromDb() throws Exception {

                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + aliceGame1.getGameId().toString()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.gameId").value(aliceGame1.getGameId().toString()))
                            .andExpect(jsonPath("$.playerName").value("Alice"))
                            .andExpect(jsonPath("$.score").value("100"));

                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + bobGame2.getGameId().toString()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.gameId").value(bobGame2.getGameId().toString()))
                            .andExpect(jsonPath("$.playerName").value("Bob"))
                            .andExpect(jsonPath("$.score").value("50"));

                }

                @Test
                void shouldReturn400WhenUuidIsInvalid() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/not-a-uuid"))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.title").value("Validation Error in Parameter"));
                }

                @Test
                void shouldReturn404GameNotFoundWhenAGameDoesNotWithProvidedUuid() throws Exception {
                    String randomUuid = UUID.randomUUID().toString();
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES + "/" + randomUuid))
                            .andExpect(status().isNotFound())
                            .andExpect(jsonPath("$.title").value("Finished Game Not Found Error"))
                            .andExpect(jsonPath("$.detail", containsString("Game Not Found")))
                            .andExpect(jsonPath("$.detail", containsString(randomUuid)));
                }
            }

            @Nested
            @DisplayName(API_FINISHED_GAMES)
            class GetGames {

                @Test
                void shouldReturnAllSortedByDefaultFinishedAtDesc() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(5)))
                            .andExpect(jsonPath("$[0].playerName").value("Alice"))
                            .andExpect(jsonPath("$[0].gameId").value(aliceGame3.getGameId().toString()))
                            .andExpect(jsonPath("$[1].playerName").value("Bob"))
                            .andExpect(jsonPath("$[1].gameId").value(bobGame1.getGameId().toString()))
                            .andExpect(jsonPath("$[2].playerName").value("Alice"))
                            .andExpect(jsonPath("$[2].gameId").value(aliceGame1.getGameId().toString()))
                            .andExpect(jsonPath("$[3].playerName").value("Alice"))
                            .andExpect(jsonPath("$[3].gameId").value(aliceGame2.getGameId().toString()))
                            .andExpect(jsonPath("$[4].playerName").value("Bob"))
                            .andExpect(jsonPath("$[4].gameId").value(bobGame2.getGameId().toString()));
                }

                @Test
                void shouldReturnGamesFilteredByPlayerIdSortedByDefaultFinishedAtDesc() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES)
                                    .param("playerId", bobId.toString()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(2)))
                            .andExpect(jsonPath("$[0].playerName").value("Bob"))
                            .andExpect(jsonPath("$[0].gameId").value(bobGame1.getGameId().toString()))
                            .andExpect(jsonPath("$[1].playerName").value("Bob"))
                            .andExpect(jsonPath("$[1].gameId").value(bobGame2.getGameId().toString()));
                }

                @Test
                void shouldReturnGamesFilteredByPlayerNameSortedByDefaultFinishedAtDesc() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES)
                                    .param("playerName", "Alice"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(3)))
                            .andExpect(jsonPath("$[0].playerName").value("Alice"))
                            .andExpect(jsonPath("$[0].gameId").value(aliceGame3.getGameId().toString()))
                            .andExpect(jsonPath("$[1].playerName").value("Alice"))
                            .andExpect(jsonPath("$[1].gameId").value(aliceGame1.getGameId().toString()))
                            .andExpect(jsonPath("$[2].playerName").value("Alice"))
                            .andExpect(jsonPath("$[2].gameId").value(aliceGame2.getGameId().toString()));
                }

                @Test
                void shouldReturnGamesSortedByScoreDesc() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES)
                                    .param("sort", "SCORE_DESC"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(5)))
                            .andExpect(jsonPath("$[0].playerName").value("Alice"))
                            .andExpect(jsonPath("$[0].gameId").value(aliceGame2.getGameId().toString()))
                            .andExpect(jsonPath("$[0].score").value(900))
                            .andExpect(jsonPath("$[1].playerName").value("Bob"))
                            .andExpect(jsonPath("$[1].gameId").value(bobGame1.getGameId().toString()))
                            .andExpect(jsonPath("$[1].score").value(700))
                            .andExpect(jsonPath("$[2].playerName").value("Alice"))
                            .andExpect(jsonPath("$[2].gameId").value(aliceGame3.getGameId().toString()))
                            .andExpect(jsonPath("$[2].score").value(500))
                            .andExpect(jsonPath("$[3].playerName").value("Alice"))
                            .andExpect(jsonPath("$[3].gameId").value(aliceGame1.getGameId().toString()))
                            .andExpect(jsonPath("$[3].score").value(100))
                            .andExpect(jsonPath("$[4].playerName").value("Bob"))
                            .andExpect(jsonPath("$[4].gameId").value(bobGame2.getGameId().toString()))
                            .andExpect(jsonPath("$[4].score").value(50));

                }

                @Test
                void shouldReturnGamesSortedByCreatedAtDesc() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES)
                                    .param("sort", "CREATED_AT_DESC"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(5)))
                            .andExpect(jsonPath("$[0].playerName").value("Bob"))
                            .andExpect(jsonPath("$[0].gameId").value(bobGame1.getGameId().toString()))
                            .andExpect(jsonPath("$[1].playerName").value("Alice"))
                            .andExpect(jsonPath("$[1].gameId").value(aliceGame1.getGameId().toString()))
                            .andExpect(jsonPath("$[2].playerName").value("Alice"))
                            .andExpect(jsonPath("$[2].gameId").value(aliceGame2.getGameId().toString()))
                            .andExpect(jsonPath("$[3].playerName").value("Bob"))
                            .andExpect(jsonPath("$[3].gameId").value(bobGame2.getGameId().toString()))
                            .andExpect(jsonPath("$[4].playerName").value("Alice"))
                            .andExpect(jsonPath("$[4].gameId").value(aliceGame3.getGameId().toString()));
                }

                @Test
                void shouldReturnBadRequestCannotSearchByPlayerNameAndPlayerId() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES)
                                    .param("sort", "CREATED_AT_DESC")
                                    .param("playerId", bobId.toString())
                                    .param("playerName", "Bob"))

                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.detail", containsString("Invalid Finished Game Search")))
                            .andExpect(jsonPath("$.detail", containsString("Cannot search by Player ID and Player Name simultaneously")));
                }

                @Test
                void shouldReturnEmptyListUnexistentPlayerId() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES)
                                    .param("sort", "CREATED_AT_DESC")
                                    .param("playerId", "99999"))

                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", empty()));
                }

                @Test
                void shouldReturnEmptyListUnexistentPlayerName() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(API_FINISHED_GAMES)
                                    .param("sort", "CREATED_AT_DESC")
                                    .param("playerName", "unexistant name"))

                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", empty()));
                }
        }
        }
}