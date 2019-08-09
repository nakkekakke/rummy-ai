package rummy.game.domain.move;

import rummy.game.domain.Layoff;
import rummy.game.domain.Player;
import rummy.game.domain.State;
import rummy.game.domain.move.Move;


public class LayoffMove extends Move {
    
    private Layoff layoff;

    public LayoffMove(Player player, State state, Layoff layoff) {
        super(player, state);
        this.layoff = layoff;
    }
    
    public Layoff getLayoff() {
        return this.layoff;
    }

    @Override
    public String type() {
        return "layoff";
    }

}
