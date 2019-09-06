package rummy.game.domain.meld;

import java.util.LinkedList;
import java.util.Objects;
import rummy.game.domain.Card;
import rummy.game.domain.Player;

/**
 * Represents a meld in the game. A meld has a player and a list of melded cards.
 */
public abstract class Meld {
    
    private Player player;
    private LinkedList<Card> cards;
    
    public Meld(Player player, LinkedList<Card> cards) {
        this.player = player;
        this.cards = cards;
    }
    
    public Meld(Meld meld) {
        this.player = meld.getPlayer();
        this.cards = meld.getCards();
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public LinkedList<Card> getCards() {
        return this.cards;
    }
    
    @Override
    public String toString() {
        return this.cards.toString();
    }
    
    /**
     * Checks if the layoff of a card is allowed.
     * @param card the card to be tested
     * @return true if the card can be laid off to this meld, otherwise false
     */
    public abstract boolean layOffAllowed(Card card);
    
    /**
     * Lays off the card to this meld.
     * @param card the card to be laid off
     */
    public abstract void layoff(Card card);
    
    /**
     * Checks if this meld is full.
     * @return true if this meld is full, otherwise false
     */
    public abstract boolean isFull();
    
    /**
     * Returns the type of this meld.
     * @return "run" if this meld is a RunMeld, "set" if this meld is a SetMeld
     */
    public abstract String type();
    
    /**
     * Creates a deep copy of this meld.
     * @return the newly created copy
     */
    public abstract Meld copy();

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.cards);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Meld other = (Meld) obj;
        
        return (this.cards.containsAll(other.cards) && other.cards.containsAll(this.cards));
    }
}
