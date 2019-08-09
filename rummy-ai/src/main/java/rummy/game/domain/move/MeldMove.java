package rummy.game.domain.move;

import rummy.game.domain.meld.Meld;
import rummy.game.domain.Player;
import rummy.game.domain.State;
import rummy.game.domain.move.Move;


public class MeldMove extends Move {
    
    private Meld meld;

    public MeldMove(Player player, State state, Meld meld) {
        super(player, state);
        this.meld = meld;
    }
    
    public Meld getMeld() {
        return this.meld;
    }

    @Override
    public String type() {
        return "meld";
    }

}
