package rummy.game.domain;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import rummy.testutil.TestUtil;

public class PlayerTest {

    private Player player;
    
    @Before
    public void setUp() {
        this.player = new Player(1);
        this.player.setHand(TestUtil.generateRandomHand(10));
    }
    
    @Test
    public void secondConstructorClonesPlayerCorrectly() {
        this.player.setPoints(25);
        Player clone = new Player(this.player);
        
        assertEquals(this.player.getId(), clone.getId());
        assertEquals(this.player.getPoints(), clone.getPoints());
        assertEquals(this.player.getHand().size(), clone.getHand().size());
        
        for (Card card : this.player.getHand()) {
            assertTrue(clone.getHand().contains(card));
        }
    }


    @Test
    public void organizeHandWorksCorrectly() {
        this.player.organizeHand();
        List<Card> hand = this.player.getHand();
        
        boolean inOrder = true;
        
        for (int i = 0; i < this.player.getHand().size() - 1; i++) {
            Card card1 = hand.get(i);
            Card card2 = hand.get(i + 1);
            if (card1.getSuit().value > card2.getSuit().value) {
                inOrder = false;
                break;
            } else if (card1.getSuit().equals(card2.getSuit().value) && card1.getRank() > card2.getRank()) {
                inOrder = false;
                break;
            }
        }
        
        assertTrue(inOrder);
    }

}