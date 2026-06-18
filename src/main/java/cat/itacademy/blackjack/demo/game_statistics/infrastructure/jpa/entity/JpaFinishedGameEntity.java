package cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.entity;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "game_number", updatable = false)
    private Integer gameNumber;

    @ManyToOne
    @JoinColumn(name = "player_id", updatable = false)
    private JpaPlayerProfileEntity userPlayer;

    @Column(name = "number_requested_cards_user_player", updatable = false)
    private Integer numberOfRequestedCardsByUserPlayer;

    @Column(name = "number_requested_cards_dealer", updatable = false)
    private Integer numberOfRequestedCardsByDealer;

    @Column(name = "total_cards_value_user_player", updatable = false)
    private Integer totalCardsValueUserPlayer;

    @Column(name = "total_cards_value_dealer", updatable = false)
    private Integer totalCardsValueDealer;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", updatable = false)
    private GameResult gameResult;

    @Column(name = "finished_with_blackjack", updatable = false)
    private Boolean finishedWithBlackjack;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false)
    private LocalDateTime finishedAt;
}