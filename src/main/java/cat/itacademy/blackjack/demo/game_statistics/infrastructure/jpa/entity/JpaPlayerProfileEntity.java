package cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "player_profile")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JpaPlayerProfileEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(updatable = false, unique = true, length = 50)
        private String name;

        @Builder.Default
        @Column(name = "won_games")
        private Integer numberOfGamesWon = 0;

        @Builder.Default
        @Column(name = "played_games")
        private Integer totalGamesPlayed = 0;

        @Builder.Default
        private Long score = 0L;
}