package rummy.game.domain.meld;

import java.util.LinkedList;
import rummy.game.domain.Card;
import rummy.game.domain.Player;

/**
 * Represents a set meld. A set meld is a group of cards having the same rank.
 */
public class SetMeld extends Meld {

    public SetMeld(Player player, LinkedList<Card> cards) {
        super(player, cards);
    }

    @Override
    public boolean isFull() {
        return super.getCards().size() == 4;
    }

    @Override
    public boolean layOffAllowed(Card card) {
        return (card.getRank() == super.getCards().getFirst().getRank()) && !isFull();
    }
    
    // FIRST CHECK IF ALLOWED !!!
    @Override
    public void layoff(Card card) {
        super.getCards().add(card);
    }
    
    @Override
    public SetMeld copy() {
        LinkedList<Card> copyCards = new LinkedList<>();
        copyCards.addAll(super.getCards());
        return new SetMeld(super.getPlayer(), copyCards);
    }

    @Override
    public String type() {
        return "set";
    }
}
