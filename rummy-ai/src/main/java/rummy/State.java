package rummy;

import java.util.List;
import java.util.Stack;

public class State {
    
    private Player currentPlayer;
    private Player waitingPlayer;
    private Card[] deck;
    private Stack<Card> discardPile;
    private List<Meld> melds;
    
    public State(Player currentPlayer, Player waitingPlayer, Card[] deck, Stack<Card> discardPile, List<Meld> melds) {
        this.currentPlayer = currentPlayer;
        this.waitingPlayer = waitingPlayer;
        this.deck = deck;
        this.discardPile = discardPile;
        this.melds = melds;
    }
    
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
    
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
    
    public Player getWaitingPlayer() {
        return this.waitingPlayer;
    }
    
    public void setWaitingPlayer(Player player) {
        this.waitingPlayer = player;
    }
    
    
    public Card[] getDeck() {
        return this.deck;
    }
    
    public void setDeck(Card[] deck) {
        this.deck = deck;
    }
    
    public Stack<Card> getDiscardPile() {
        return this.discardPile;
    }
    
    public void setDiscardPile(Stack<Card> discardPile) {
        this.discardPile = discardPile;
    }
    
    public List<Meld> getMelds() {
        return this.melds;
    }
    
    public void setMelds(List<Meld> melds) {
        this.melds = melds;
    }
}
