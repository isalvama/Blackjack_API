package cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "games")
public class GameDocument {

        @Id
        private String id;
        private LocalDateTime lastTimePlayedAt;
        private String userName;
        private PlayerDocument userPlayerInfo;
        private PlayerDocument dealerInfo;
        private String gameState;
        private List<CardDocument> deck;
        private LocalDateTime createdAt;


    protected GameDocument() {
        }

        public GameDocument(String id,
                            String userName,
                            PlayerDocument userPlayerInfo,
                            PlayerDocument dealerInfo,
                            String gameState,
                            List<CardDocument> deck
        ) {
            this.id = id;
            this.userName = userName;
            this.userPlayerInfo = userPlayerInfo;
            this.dealerInfo = dealerInfo;
            this.gameState = gameState;
            this.deck = deck;
        }
}
