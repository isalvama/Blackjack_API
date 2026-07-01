This is an expanded and technically detailed **README.md** tailored for your project. It highlights the strategic use of **Hexagonal Architecture**, **Domain-Driven Design (DDD)**, and the specific functional capabilities of each API module.

---

# 🃏 Blackjack API: Advanced Hexagonal Architecture & Hybrid Persistence

## 🎯 Project Overview
This project is a full-featured Blackjack game engine built with **Spring Boot 3**. It is designed to demonstrate high-level software engineering patterns, specifically **Hexagonal Architecture (Ports and Adapters)** and **Domain-Driven Design (DDD)**.

The system utilizes a **Hybrid Persistence Strategy**:
1.  **MongoDB**: Handles high-velocity, transient data (Ongoing games).
2.  **MySQL**: Handles immutable historical records and relational statistics (Rankings and Profiles).

---

## 🏗️ Architectural Philosophy

### 🔷 Hexagonal Architecture (Ports & Adapters)
The application is structured to ensure that the **Core Domain** is completely isolated from external technologies (databases, web frameworks, message brokers).
*   **Domain Layer**: The "Heart" of the software. It contains the business rules and has zero dependencies on external libraries.
*   **Application Layer**: Orchestrates the flow of data. It defines **Input Ports** (Use Cases) that the Web layer calls, and **Output Ports** (Interfaces) that the Persistence layer implements.
*   **Infrastructure Layer**: Contains the **Adapters**. This is where the concrete implementations reside (REST Controllers, MongoDB Repositories, MySQL/JPA Repositories).

### 🧠 Domain-Driven Design (DDD) Implementation
We have applied DDD tactical patterns to model the Blackjack complexity:
*   **Aggregates**: `Game` acts as the Aggregate Root. It ensures consistency across the `Deck`, `Dealer`, and `UserPlayer`.
*   **Entities**: `Player` (and its subclasses) and `Deck` have unique identities and complex lifecycles.
*   **Value Objects**: `Card`, `Name`, `GameId`, and `HandState`. These are immutable objects defined by their attributes. For example, `Name` self-validates and formats strings upon creation.
*   **Domain Services**: Logic that doesn't naturally fit into an entity, such as `ShuffleStrategy`.

---

## 💾 Hybrid Persistence Strategy
One of the core strengths of this API is its ability to handle different data lifecycles across different storage engines:

| Feature | Technology | Justification |
| :--- | :--- | :--- |
| **Active Games** | **MongoDB** | Active games are "Hot Data." They require fast writes/updates as players hit or stand. Being document-oriented allows for a flexible schema that captures the entire game state in a single fetch. |
| **Finished Games** | **MySQL** | Once a game is over, it becomes "Cold Data." MySQL provides ACID compliance for historical records and allows for complex relational queries when generating statistics. |
| **Player Profiles** | **MySQL** | Profiles require strong consistency to maintain accurate leaderboards and win/loss ratios across thousands of finished matches. |

---

## 🔄 Asynchronous Event-Driven Flow
The two persistence worlds are bridged by a **Domain Event System**:
1.  When a game reaches the `OVER` state, a `GameFinishedEvent` is published.
2.  An asynchronous **EventListener** (`@Async`) intercepts this event.
3.  The **ProcessFinishedGameService** executes:
    *   It creates or updates the **PlayerProfile** in MySQL.
    *   It archives the game details in the `finished_game` table.
    *   It triggers the deletion of the game from **MongoDB**, as it is no longer active.
4.  This ensures the player experiences zero latency during the game, as heavy database operations happen in the background.

---

## 🛣️ API Endpoints Reference

### 1. Active Games Management (`/api/active-games`)
*   `POST /`: Starts a new game. Handles "Natural Blackjack" immediately.
*   `GET /`: Lists all games currently in progress.
*   `GET /{id}`: Retrieves the full state of a specific active game.
*   `POST /{id}/hit`: Player draws a card. If the total > 21, the game transitions to `OVER`.
*   `POST /{id}/stand`: Player ends their turn. Triggers the Dealer's AI logic.
*   `DELETE /{id}`: Manually cancels/removes an active game.

### 2. Finished Games History (`/api/finished-games`)
*   `GET /`: Returns a sorted list of all archived games.
    *   *Features*: Filtering by `playerId` OR `playerName` (mutually exclusive).
    *   *Sorting*: Support for `score`, `finishedAt`, and `createdAt`.
*   `GET /{id}`: Retrieves a detailed record of a finished match by its UUID.

### 3. Player Profiles & Rankings (`/api/player-profiles`)
*   `GET /`: The "Leaderboard" endpoint. Returns profiles sorted by score, wins, or games played.
*   `GET /{id}`: Get statistics for a specific player by their numeric ID.
*   `GET /search?name=...`: Find a player's profile and stats by their username.

---

## 📜 Blackjack Rules Implemented
*   **The Dealer**: Must hit until reaching at least 17.
*   **The Deck**: Standard 52-card deck, shuffled at the start of every game.
*   **Ace Logic**: Dynamically switches between 11 and 1 to prevent the player from busting while maximizing the score.
*   **Blackjack**: A "Natural" 21 (Ace + 10/J/Q/K) pays out a higher score and ends the turn immediately.

---

## 🚀 Getting Started

### 📋 Prerequisites
*   **Java 21**
*   **Docker & Docker Compose**
*   **Maven**

### 1. Environment Configuration
Create a `.env` file in the root directory:
```env
PROD_MYSQL_PORT=3308
PROD_MYSQL_DATABASE=blackjack
PROD_MYSQL_ROOT_PASSWORD=blackjack_pass

PROD_MONGO_PORT=27017
MONGO_PROD_USER=admin
MONGO_PROD_PASS=password123
MONGO_PROD_DB=blackjack_mongo

APP_PORT=8080
```

### 2. Running with Docker (Recommended)
```bash
mvn clean package -DskipTests
docker compose up --build
```

### 3. API Documentation (Swagger)
Explore and test the API directly from your browser:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

---

## 🧪 Testing Strategy
The project maintains high reliability through:
*   **Unit Tests**: Testing domain logic (Hand calculation, Dealer AI) in isolation.
*   **Integration Tests (Testcontainers)**: We spin up real **MySQL** and **MongoDB** containers for every test suite to ensure the persistence layer works exactly like production.
*   **Asynchronous Testing**: Using **Awaitility** to verify that background statistics are updated correctly after a game ends.

To run the suite:
```bash
mvn test
```