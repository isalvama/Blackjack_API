package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidHitException;
import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.active_game.domain.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.active_game.domain.GameState;
import cat.itacademy.blackjack.demo.active_game.domain.exception.ActiveGameException;
import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidActiveGameException;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.GameOutcome;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Game {
    private GameId id;
    private GameState gameState;
    private UserPlayer userPlayer;
    private Dealer dealer;
    private Deck deck;
    private GameOutcome gameOutcome;
    private LocalDateTime createdAt;
    private LocalDateTime lastTimePlayedAt;

    private Game(GameId id, UserPlayer userPlayer, Dealer dealer, Deck deck) {
        if (deck.getCards().isEmpty()){
            throw new InvalidActiveGameException("deck's list of cards cannot be empty");
        }
        this.id = validateNotNull(id, "id cannot be null");
        this.userPlayer = validateNotNull(userPlayer, "userPlayer cannot be null");
        this.dealer = validateNotNull(dealer, "dealer cannot be null");
        this.deck = validateNotNull(deck, "deck cannot be null");
    }

    public static Game create(UserPlayer userPlayer, Dealer dealer, Deck deck) {
       Game game = new Game(
               GameId.generate(),
               userPlayer,
               dealer,
               deck
       );
       game.gameState = GameState.STARTED;
       return game;
    }

    public static Game reconstitute(GameId id, GameState gameState, UserPlayer userPlayer, Dealer dealer, Deck deck, LocalDateTime createdAt, LocalDateTime lastTimePlayedAt) {
         Game game = new Game(
                id,
                userPlayer,
                dealer,
                deck
        );
         game.gameState = validateNotNull(gameState, "gameState cannot be null");
         game.createdAt = validateNotNull(createdAt, "createdAt cannot be null");
         game.lastTimePlayedAt = validateNotNull(lastTimePlayedAt, "lastTimePlayedAt cannot be null");
        return game;
    }

    public void updateAuditInfo(LocalDateTime createdAt, LocalDateTime lastTimePlayedAt){
        this.createdAt = validateNotNull(createdAt, "createdAt cannot be null");
        this.lastTimePlayedAt = validateNotNull(lastTimePlayedAt, "lastTimePlayedAt cannot be null");
    }

    public void updateAuditInfo(LocalDateTime lastTimePlayedAt){
        this.lastTimePlayedAt = validateNotNull(lastTimePlayedAt, "lastTimePlayedAt cannot be null");
    }

    public void start (ShuffleStrategy shuffleStrategy){
        if (gameState != GameState.STARTED){
            throw new ActiveGameException("the Game cannot start over because it has already started");
        }
        this.deck.shuffle(shuffleStrategy);
        dealerHits();
        playerHits();
        playerHits();
        if (this.userPlayer.checkBlackjack()){
            dealerHits();
            setFinalGameResult(determineBlackjackWinner(), true);
            }
        }

    public void playerRequestedHit(){
        ensureGameIsActive();
        playerHits();
        if (this.userPlayer.totalValueIsGreaterThan21()){
            setFinalGameResult(GameResult.DEALER_WIN, false);
        }
    }

    public void stand (){
        ensureGameIsActive();
        dealerTurn();
        if (this.dealer.checkBlackjack()){
            setFinalGameResult(GameResult.DEALER_WIN, true);
            return;
        }
        if (this.dealer.totalValueIsGreaterThan21()){
            setFinalGameResult(GameResult.USER_WIN, false);
            return;
        }
        setFinalGameResult(determineWinner(), false);
    }

    private void playerHits (){
        ensureDeckHasCards();
        if (gameState == GameState.OVER) return;
        Card card = this.deck.draw();
        this.userPlayer.hit(card);
    }

    private void dealerTurn(){
        while (!this.dealer.shouldStand() && this.gameState != GameState.OVER && !this.deck.hasNoCards() && !this.dealer.checkBlackjack()){
            dealerHits();
        }
    }

    public void dealerHits () {
        ensureDeckHasCards();
        if (gameState == GameState.OVER) return;
        Card card = this.deck.draw();
        this.dealer.hit(card);
    }

    private void ensureGameIsActive(){
        if (gameState == GameState.OVER) {
            throw new InvalidHitException("the player cannot hit a new card because the game is already over");
        }
    }

    private void ensureDeckHasCards(){
        if (this.deck.hasNoCards()) {
            setFinalGameResult(determineWinner(), false);
        }
    }

    private void setFinalGameResult(GameResult gameResult, boolean finishedWithBlackJack){
        this.gameState = GameState.OVER;
        this.gameOutcome = new GameOutcome(
                gameResult,
                finishedWithBlackJack
        );
    }

    private GameResult determineBlackjackWinner(){
        if (!this.dealer.checkBlackjack() && !this.userPlayer.checkBlackjack()){
            throw new IllegalCallerException("determineBlackjackWinner() method should not be called because any player has finished with blackjack");
        }
        if (this.dealer.checkBlackjack() && this.userPlayer.checkBlackjack()){
            return GameResult.TIE;
        }
        if (this.dealer.checkBlackjack()){
            return GameResult.DEALER_WIN;
        } else {
            return GameResult.USER_WIN;
        }
    }

    private GameResult determineWinner(){
        int playerHandValue = this.userPlayer.getHandValue();
        int dealerHandValue = this.dealer.getHandValue();
        if (playerHandValue > dealerHandValue){
            return GameResult.USER_WIN;
        }
        if (dealerHandValue > playerHandValue){
            return GameResult.DEALER_WIN;
        }
        return GameResult.TIE;
    }


    private static <T> T validateNotNull(T obj, String message) {
        if (obj == null)
            throw new InvalidActiveGameException(message);
        return obj;
    }
}
