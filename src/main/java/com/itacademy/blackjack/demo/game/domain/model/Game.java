package cat.itacademy.blackjack.game.domain.model;

import cat.itacademy.blackjack.common.domain.GameResult;
import cat.itacademy.blackjack.game.application.service.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.game.domain.GameState;
import cat.itacademy.blackjack.game.domain.exception.GameException;
import cat.itacademy.blackjack.game.domain.exception.InvalidGameException;
import cat.itacademy.blackjack.game.domain.exception.InvalidHitException;
import cat.itacademy.blackjack.game.domain.value_object.Card;
import cat.itacademy.blackjack.common.domain.value_object.GameId;
import cat.itacademy.blackjack.game.domain.value_object.GameOutcome;

import java.time.LocalDateTime;

//annotations getter
public class Game {
    private GameId id;
    private GameState gameState;
    private UserPlayer userPlayer;
    private Dealer dealer;
    private Deck deck;
    private LocalDateTime createdAt;
    private LocalDateTime lastTimePlayedAt; // passarho per repo o JPA
    private GameOutcome gameOutcome;

    public Game(GameId id, GameState gameState, UserPlayer userPlayer, Dealer dealer, Deck deck, LocalDateTime createdAt, LocalDateTime lastTimePlayedAt, GameOutcome gameOutcome) {
        if (id == null){
            throw new InvalidGameException("id cannot be null");
        }
        if (gameState == null){
            throw new InvalidGameException("game state cannot be null");
        }
        if (userPlayer == null){
            throw new InvalidGameException("user player cannot be null");
        }
        if (dealer == null){
            throw new InvalidGameException("dealer cannot be null");
        }
        if (deck == null){
            throw new InvalidGameException("deck cannot be null");
        }
        if (deck.getCards().isEmpty()){
            throw new InvalidGameException("deck's list of cards cannot be empty");
        }
        if (createdAt == null){
            throw new InvalidGameException("createdAt cannot be null");
        }

        // treure
        this.id = id;
        this.gameState = gameState;
        this.userPlayer = userPlayer;
        this.dealer = dealer;
        this.deck = deck;
        this.createdAt = createdAt;
        this.lastTimePlayedAt = lastTimePlayedAt;
        this.gameOutcome = gameOutcome;
    }

    public static Game create(UserPlayer userPlayer, Dealer dealer, Deck deck) {
       return new Game(
               GameId.generate(),
               GameState.STARTED, // settejar
               userPlayer,
               dealer,
               deck,
               LocalDateTime.now(),
               LocalDateTime.now(),
               null
       );
       //settejar createdAt lastTimePlayed o createdAt al JPA
    }
    // named constructor
    public void start (ShuffleStrategy shuffleStrategy){
        if (gameState != GameState.STARTED){
            throw new GameException("the Game cannot start over because it has already started");
        }
        this.deck.shuffle(shuffleStrategy);
        dealerHits();
        playerHits();
        playerHits();
        if (this.userPlayer.checkBlackjack()){
            dealerTurn();
            setFinalGameResult(determineBlackjackWinner(), true);
            }
        }

    public void dealerTurn(){
        while (!this.dealer.stand()){
            dealerHits();
        }
    }

    public void dealerHits () {
        ensureGameIsActive();
        Card card = drawDeck();
        this.dealer.hit(card);
    }

    public void playerHits (){
        ensureGameIsActive();
        Card card = drawDeck();
        this.userPlayer.hit(card);
        this.lastTimePlayedAt = LocalDateTime.now();
    }

    private void ensureGameIsActive(){
        if (gameState == GameState.OVER){
            throw new InvalidHitException("the player cannot hit a new card because the game is already over");
        }
    }

    private Card drawDeck(){
        if (this.deck.getCards().isEmpty()){
            setFinalGameResult(determineWinner(), false);
        }
        // metode per checkear si es empty
        return this.deck.draw();
    }

    private void setFinalGameResult(GameResult gameResult, boolean finishedWithBlackJack){
        this.gameState = GameState.OVER;
        this.gameOutcome = new GameOutcome(
                gameResult,
                finishedWithBlackJack,
                LocalDateTime.now()
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
        int dealerHandValue = this.userPlayer.getHandValue();
        if (playerHandValue > dealerHandValue){
            return GameResult.USER_WIN;
        }
        if (dealerHandValue > playerHandValue){
            return GameResult.DEALER_WIN;
        }
        return GameResult.TIE;
    }

    public GameId getId() {
        return id;
    }

    public GameState getGameState() {
        return gameState;
    }

    public UserPlayer getUserPlayer() {
        return userPlayer;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public Deck getDeck() {
        return deck;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastTimePlayedAt() {
        return lastTimePlayedAt;
    }

    public GameOutcome getGameOutcome() {
        return gameOutcome;
    }
}
