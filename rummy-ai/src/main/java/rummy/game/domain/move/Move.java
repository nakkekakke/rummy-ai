package rummy.game.domain.move;

import rummy.game.domain.Player;


public abstract class Move {
    
    private final Player player;
    
    public Move(Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }

    
    public abstract String type();
}
