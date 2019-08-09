package rummy.game.domain.meld;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import rummy.game.domain.Card;
import rummy.game.domain.Player;
import rummy.game.domain.Suit;
import rummy.testutil.TestUtil;

public class MeldTest {
    
    private Player player;
    private RunMeld runMeld;
    private SetMeld setMeld;

    @Before
    public void setUp() {
        this.player = new Player(1);
    }


    @Test
    public void runMeldLayOffCheckWorksCorrectly() {
        setUpRunMeld();
        
        Card legal1 = new Card(Suit.CLUBS, 3);
        Card legal2 = new Card(Suit.CLUBS, 8);
        Card illegal1 = new Card(Suit.CLUBS, 13);
        Card illegal2 = new Card(Suit.SPADES, 8);
        
        assertTrue(this.runMeld.layOffAllowed(legal1));
        assertTrue(this.runMeld.layOffAllowed(legal2));
        assertFalse(this.runMeld.layOffAllowed(illegal1));
        assertFalse(this.runMeld.layOffAllowed(illegal2));
    }
    
    @Test
    public void setMeldLayOffCheckWorksCorrectly() {
        setUpSetMeld();
        
        Card legal1 = new Card(Suit.CLUBS, 10);
        Card legal2 = new Card(Suit.DIAMONDS, 10);
        Card illegal1 = new Card(Suit.CLUBS, 9);
        Card illegal2 = new Card(Suit.SPADES, 1);
        
        assertTrue(this.setMeld.layOffAllowed(legal1));
        assertTrue(this.setMeld.layOffAllowed(legal2));
        assertFalse(this.setMeld.layOffAllowed(illegal1));
        assertFalse(this.setMeld.layOffAllowed(illegal2));
    }
    
    @Test
    public void illegalToAddToAFullSetMeld() {
        setUpFullSetMeld();
        
        Card card = new Card(Suit.DIAMONDS, 10);
        
        assertFalse(this.setMeld.layOffAllowed(card));
    }
    
    @Test
    public void runMeldLayoffWorksCorrectly() {
        setUpRunMeld();
        int originalSize = this.runMeld.getCards().size();
        
        Card low1 = new Card(Suit.CLUBS, 3);
        Card low2 = new Card(Suit.CLUBS, 2);
        Card high1 = new Card(Suit.CLUBS, 8);
        Card high2 = new Card(Suit.CLUBS, 9);
        
        this.runMeld.layoff(low1);
        this.runMeld.layoff(low2);
        
        this.runMeld.layoff(high1);
        this.runMeld.layoff(high2);
        
        assertEquals(originalSize + 4, this.runMeld.getCards().size());
        assertEquals(low2, this.runMeld.getCards().getFirst());
        assertEquals(low1, this.runMeld.getCards().get(1));
        assertEquals(high2, this.runMeld.getCards().getLast());
        assertEquals(high1, this.runMeld.getCards().get(originalSize + 4 - 2));
    }
    
    @Test
    public void setMeldLayoffWorksCorrectly() {
        setUpSetMeld();
        int originalSize = this.setMeld.getCards().size();
        
        Card card = new Card(Suit.CLUBS, 10);
        
        this.setMeld.layoff(card);
        
        assertEquals(originalSize + 1, this.setMeld.getCards().size());
        assertTrue(this.setMeld.getCards().contains(card));
    }
    
    private void setUpRunMeld() {
        this.runMeld = new RunMeld(player, TestUtil.generateRunMeldCards(4, 7, Suit.CLUBS));
    }
    
    private void setUpSetMeld() {
        this.setMeld = new SetMeld(player, TestUtil.generateSetMeldCards(10, 3));
    }
    
    private void setUpFullSetMeld() {
        this.setMeld = new SetMeld(player, TestUtil.generateSetMeldCards(10, 4));
    }
}