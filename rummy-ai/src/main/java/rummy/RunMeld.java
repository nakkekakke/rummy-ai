package rummy;

import java.util.LinkedList;
import java.util.List;

public class RunMeld extends Meld {

    public RunMeld(Player player, LinkedList<Card> cards) {
        super(player, cards);
    }

    @Override
    public boolean isFull() {
        return super.getCards().size() == 13;
    }

    @Override
    public boolean layOffAllowed(Card card) {
        Card first = super.getCards().getFirst();
        Card last = super.getCards().getLast();
        return card.getSuit().equals(first.getSuit()) && (( card.getValue() == first.getValue() - 1 ) || ( card.getValue() == last.getValue() + 1 ));
    }

    // FIRST CHECK IF ALLOWED !!!
    @Override
    public void layOff(Card card) {
        Card first = super.getCards().getFirst();
        Card last = super.getCards().getLast();
        if (card.getValue() == first.getValue() - 1) {
            super.getCards().addFirst(card);
        } else if (card.getValue() == last.getValue() + 1) {
            super.getCards().addLast(card);
        }
    }
    
    
}
