package rummy.game.domain;

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
}
