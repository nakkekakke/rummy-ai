package rummy.game.domain;

import java.util.Objects;
import rummy.game.domain.meld.Meld;

// Represents a layoff (adding a card to a pre-existing meld)
public class Layoff {
    
    private final Card card;
    private final Meld meld;
    
    public Layoff(Card card, Meld meld) {
        this.card = card;
        this.meld = meld;
    }
    
    public Card getCard() {
        return this.card;
    }
    
    public Meld getMeld() {
        return this.meld;
    }
    
    public Layoff copy() {
        return new Layoff(this.card, this.meld.copy());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.card);
        hash = 79 * hash + Objects.hashCode(this.meld);
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
        final Layoff other = (Layoff) obj;
        return this.card.equals(other.card) && this.meld.equals(other.meld);
    }
    
}
