package rummy.game.domain.move;

import java.util.Objects;
import rummy.game.domain.meld.Meld;
import rummy.game.domain.Player;

/**
 * Represents a move where the player melds a set of cards from their hand.
 */
public class MeldMove extends Move {
    
    private final Meld meld;

    public MeldMove(Player player, Meld meld) {
        super(player);
        this.meld = meld;
    }
    
    public Meld getMeld() {
        return this.meld;
    }
    
    @Override
    public MeldMove copy() {
        return new MeldMove(super.getPlayer(), this.meld.copy());
    }

    @Override
    public String type() {
        return "meld";
    }
    
    @Override
    public String toString() {
        return "Meld " + meld;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.meld);
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
        final MeldMove other = (MeldMove) obj;
        return this.meld.equals(other.meld);
    }
}
