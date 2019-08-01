package rummy;


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
}
