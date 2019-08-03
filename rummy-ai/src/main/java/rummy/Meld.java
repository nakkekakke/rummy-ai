package rummy;

import java.util.LinkedList;


public abstract class Meld {
    
    private Player player;
    private LinkedList<Card> cards;
    
    public Meld(Player player, LinkedList<Card> cards) {
        this.player = player;
        this.cards = cards;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public LinkedList<Card> getCards() {
        return this.cards;
    }
    
    public abstract boolean layOffAllowed(Card card);
    
    public abstract void layoff(Card card);
    
    public abstract boolean isFull();
    
    public abstract String type();
}
