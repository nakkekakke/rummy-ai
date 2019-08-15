package rummy.game.domain.move;

import java.util.Objects;
import rummy.game.domain.move.Move;
import rummy.game.domain.Card;
import rummy.game.domain.Player;


public class DiscardMove extends Move {
    
    private Card card; // card to discard from hand

    public DiscardMove(Player player, Card card) {
        super(player);
        this.card = card;
    }
    
    public Card getCard() {
        return this.card;
    }

    @Override
    public String type() {
        return "discard";
    }
    
    @Override
    public String toString() {
        return "Discard " + this.card;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.card);
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
        final DiscardMove other = (DiscardMove) obj;
        if (!Objects.equals(this.card, other.card)) {
            return false;
        }
        return true;
    }
    
}
