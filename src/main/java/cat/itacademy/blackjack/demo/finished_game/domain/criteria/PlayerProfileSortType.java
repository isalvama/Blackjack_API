package cat.itacademy.blackjack.demo.finished_game.domain.criteria;

import lombok.Getter;

@Getter
public enum PlayerProfileSortType {
    SCORE_DESC("score", OrderType.DESC),
    NUMBER_OF_GAMES_PLAYED_DESC("totalGamesPlayed", OrderType.DESC),
    NUMBER_OF_GAMES_WON_DESC("numberOfGamesWon", OrderType.DESC);

    private final String entityProperty;
    private final OrderType orderType;

    PlayerProfileSortType(String entityProperty, OrderType orderType){
        this.entityProperty = entityProperty;
        this.orderType = orderType;
    }
    public static final String DEFAULT_SORT_VALUE = "SCORE_DESC";
}
