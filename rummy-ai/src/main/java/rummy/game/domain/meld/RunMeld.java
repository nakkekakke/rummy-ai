package rummy.game.domain.meld;

import java.util.LinkedList;
import rummy.game.domain.Card;
import rummy.game.domain.Player;

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
        return card.getSuit().equals(first.getSuit()) && (( card.getRank() == first.getRank() - 1 ) || ( card.getRank() == last.getRank() + 1 ));
    }

    // FIRST CHECK IF ALLOWED !!!
    @Override
    public void layoff(Card card) {
        Card first = super.getCards().getFirst();
        Card last = super.getCards().getLast();
        if (card.getRank() == first.getRank() - 1) {
            super.getCards().addFirst(card);
        } else if (card.getRank() == last.getRank() + 1) {
            super.getCards().addLast(card);
        }
    }
    
    @Override
    public RunMeld copy() {
        LinkedList<Card> copyCards = new LinkedList<>();
        copyCards.addAll(super.getCards());
        return new RunMeld(super.getPlayer(), copyCards);
    }

    @Override
    public String type() {
        return "run";
    }
}
