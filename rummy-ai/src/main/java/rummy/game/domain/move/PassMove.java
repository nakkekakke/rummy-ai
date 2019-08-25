package rummy.game.domain.move;

import java.util.Objects;
import rummy.game.domain.Player;


public class PassMove extends Move {
    
    private final String phase;
    
    public PassMove(Player player, String phase) {
        super(player);
        this.phase = phase;
    }
    
    public String getPhase() {
        return this.phase;
    }
    
    @Override
    public PassMove copy() {
        return new PassMove(super.getPlayer(), this.phase);
    }

    @Override
    public String type() {
        return "pass";
    }
    
    @Override
    public String toString() {
        return "Pass";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.phase);
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
        final PassMove other = (PassMove) obj;
        return this.phase.equals(other.phase);
    }
    
}
