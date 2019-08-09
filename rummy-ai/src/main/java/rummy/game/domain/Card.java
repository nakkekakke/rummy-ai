package rummy.game.domain;

import rummy.game.domain.Suit;
import java.util.Objects;


public class Card {
    
    private final Suit suit;
    private final int rank;
    
    public Card(Suit suit, int rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    public Suit getSuit() {
        return this.suit;
    }
    
    public int getValue() {
        return this.rank;
    }
    
    @Override
    public String toString() {
        return this.rank + " of " + this.suit;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.suit);
        hash = 13 * hash + this.rank;
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
        final Card other = (Card) obj;
        if (this.rank != other.rank) {
            return false;
        }
        if (this.suit != other.suit) {
            return false;
        }
        return true;
    }
    
    
}
