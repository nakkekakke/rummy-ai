package rummy;


public enum Suit {
    CLUBS(1),
    SPADES(2),
    DIAMONDS(3),
    HEARTS(4);
    
    public int value;
    
    Suit(int value) {
        this.value = value;
    }
}
