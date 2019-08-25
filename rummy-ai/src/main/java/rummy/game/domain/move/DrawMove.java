package rummy.game.domain.move;

import rummy.game.domain.Player;


public class DrawMove extends Move {
    
    private final boolean isDeckDraw; // true if deck draw, false if discard pile draw

    public DrawMove(Player player, boolean isDeckDraw) {
        super(player);
        this.isDeckDraw = isDeckDraw;
    }
    
    public boolean isDeckDraw() {
        return this.isDeckDraw;
    }
    
    @Override
    public DrawMove copy() {
        return new DrawMove(super.getPlayer(), this.isDeckDraw);
    }

    @Override
    public String type() {
        return "draw";
    }
    
    @Override
    public String toString() {
        if (isDeckDraw) {
            return "Draw from deck";
        }
        
        return "Draw from discard pile";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.isDeckDraw ? 1 : 0);
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
        final DrawMove other = (DrawMove) obj;
        return this.isDeckDraw == other.isDeckDraw;
    }
    
}
