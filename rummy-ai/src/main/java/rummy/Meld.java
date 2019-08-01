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
    
    abstract boolean layOffAllowed(Card card);
    
    abstract void layOff(Card card);
    
    abstract boolean isFull();
}
