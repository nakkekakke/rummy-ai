package rummy.game.domain.meld;

import java.util.LinkedList;
import java.util.Objects;
import rummy.game.domain.Card;
import rummy.game.domain.Player;


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
    
    public abstract boolean layOffAllowed(Card card);
    
    public abstract void layoff(Card card);
    
    public abstract boolean isFull();
    
    public abstract String type();

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
        if (!Objects.equals(this.cards, other.cards)) {
            return false;
        }
        return true;
    }
    
    
}
