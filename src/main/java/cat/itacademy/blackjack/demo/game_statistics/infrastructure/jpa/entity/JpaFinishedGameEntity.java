package cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.entity;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "finished_game")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JpaFinishedGameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", updatable = false, unique = true)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID gameId;

    @ManyToOne
    @JoinColumn(name = "player_id", updatable = false)
    private JpaPlayerProfileEntity player;

    @Column(name = "number_requested_cards_user_player", updatable = false)
    private Integer playerNumberOfCards;

    @Column(name = "number_requested_cards_dealer", updatable = false)
    private Integer dealerNumberOfCards;

    @Column(name = "total_cards_value_user_player", updatable = false)
    private Integer totalCardsValuePlayer;

    @Column(name = "total_cards_value_dealer", updatable = false)
    private Integer totalCardsValueDealer;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", updatable = false)
    private GameResult gameResult;

    @Column(name = "finished_with_blackjack", updatable = false)
    private Boolean finishedWithBlackjack;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreatedDate
    @Column(name = "finished_at", updatable = false)
    private LocalDateTime finishedAt;


    @PrePersist
    protected void onUpdate() {
        this.finishedAt = LocalDateTime.now();
    }
}