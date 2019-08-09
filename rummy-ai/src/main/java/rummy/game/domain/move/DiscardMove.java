package rummy.game.domain.move;

import rummy.game.domain.move.Move;
import rummy.game.domain.Card;
import rummy.game.domain.Player;
import rummy.game.domain.State;


public class DiscardMove extends Move {
    
    private Card card; // card to discard from hand

    public DiscardMove(Player player, State state, Card card) {
        super(player, state);
        this.card = card;
    }
    
    public Card getCard() {
        return this.card;
    }

    @Override
    public String type() {
        return "discard";
    }

}
