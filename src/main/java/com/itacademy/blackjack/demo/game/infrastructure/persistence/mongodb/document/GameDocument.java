package cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.document;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Document(collection = "games")
public class GameDocument {

        @Id
        private String id;
        private LocalDateTime createdAt;
        private LocalDateTime lastTimePlayedAt;
        private String userName;
        private PlayerDocument userPlayerInfo;
        private PlayerDocument dealerInfo;
        private String gameState;
        private List<CardDocument> deck;


    protected GameDocument() {
        }

        public GameDocument(String id, LocalDateTime createdAt, LocalDateTime lastTimePlayedAt,
                            String userName, PlayerDocument userPlayerInfo, PlayerDocument dealerInfo, String gameState, List<CardDocument> deck) {
            this.id = id;
            this.createdAt = createdAt;
            this.lastTimePlayedAt = lastTimePlayedAt;
            this.userName = userName;
            this.userPlayerInfo = userPlayerInfo;
            this.dealerInfo = dealerInfo;
            this.gameState = gameState;
            this.deck = deck;
        }


}
