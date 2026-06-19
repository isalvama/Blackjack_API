package cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_profile")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JpaPlayerProfileEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(updatable = false, nullable = false)
        private Long id;

        @Column(updatable = false, unique = true, length = 50, nullable = false)
        private String name;

        @Column(name = "played_games", nullable = false)
        private Long totalGamesPlayed;

        @Column(name = "won_games", nullable = false)
        private Long numberOfGamesWon;

        @Column(nullable = false)
        private Long score;
}