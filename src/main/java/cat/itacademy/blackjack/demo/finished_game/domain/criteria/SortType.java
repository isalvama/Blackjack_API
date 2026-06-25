package cat.itacademy.blackjack.demo.finished_game.domain.criteria;

import lombok.Getter;

@Getter
public enum SortType {
    FINISHED_AT_DESC("finishedAt", OrderType.DESC),
    SCORE_DESC("score", OrderType.DESC);

    private final String entityProperty;
    private final OrderType orderType;

    SortType(String entityProperty, OrderType orderType){
        this.entityProperty = entityProperty;
        this.orderType = orderType;
    }
    public static final String DEFAULT_SORT_VALUE = "FINISHED_AT_DESC";
}
