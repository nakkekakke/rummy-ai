package rummy;

import java.util.LinkedList;
import java.util.List;


public class SetMeld extends Meld {

    public SetMeld(Player player, LinkedList<Card> cards) {
        super(player, cards);
    }

    @Override
    boolean isFull() {
        return super.getCards().size() == 4;
    }

    @Override
    boolean layOffAllowed(Card card) {
        return (card.getValue() == super.getCards().getFirst().getValue()) && !isFull();
    }
    
    // FIRST CHECK IF ALLOWED !!!
    @Override
    void layOff(Card card) {
        super.getCards().add(card);
    }

}
