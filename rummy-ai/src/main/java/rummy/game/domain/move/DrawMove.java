package rummy.game.domain.move;

import rummy.game.domain.move.Move;
import rummy.game.domain.Player;
import rummy.game.domain.State;


public class DrawMove extends Move {
    
    private boolean isDeckDraw; // true if deck draw, false if discard pile draw

    public DrawMove(Player player, State state, boolean isDeckDraw) {
        super(player, state);
        this.isDeckDraw = isDeckDraw;
    }
    
    public boolean isDeckDraw() {
        return this.isDeckDraw;
    }

    @Override
    public String type() {
        return "draw";
    }

}
