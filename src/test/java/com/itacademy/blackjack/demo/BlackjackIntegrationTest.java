package com.itacademy.blackjack.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BlackjackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
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

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST " + BASE_API)
    class StartGame {

        @DisplayName("should return 201 with information about the game state when name data is valid")
        @Test
        void shouldStartGame() throws Exception {
            cat.itacademy.blackjack.game.infrastructure.web.dto.CreateGameDto createGameDto = new cat.itacademy.blackjack.game.infrastructure.web.dto.CreateGameDto("test name");

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_API)
                    .contentType(MediaType.APPLICATION_JSON) // Corregido: Import de Spring
                    .content(objectMapper.writeValueAsString(createGameDto)));

            result.andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString(BASE_API + "/")))
                    .andExpect(jsonPath("$.id").exists());
        }
    }
}