package rummy.game.domain.move;

import rummy.game.domain.Player;

/**
 * An abstract class representing a move made by a player. 
 */
public abstract class Move {
    
    private final Player player;
    
    public Move(Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    /**
     * Creates a deep copy of this move.
     * @return the newly created copy
     */
    public abstract Move copy();
    
    /**
     * Returns the type of this move.
     * @return "draw" if this move is a DrawMove, "meld" if this move is a MeldMove, "layoff" if LayoffMove, "discard" if DiscardMove and "pass" if PassMove 
     */
    public abstract String type();
}
