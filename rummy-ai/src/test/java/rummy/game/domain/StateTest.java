package rummy.game.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import rummy.game.domain.meld.Meld;
import rummy.game.domain.meld.RunMeld;
import rummy.game.domain.meld.SetMeld;
import rummy.game.domain.move.DiscardMove;
import rummy.game.domain.move.DrawMove;
import rummy.game.domain.move.LayoffMove;
import rummy.game.domain.move.MeldMove;
import rummy.game.domain.move.Move;
import rummy.game.domain.move.PassMove;
import rummy.testutil.TestUtil;

public class StateTest {

    private State state;

    @Before
    public void setUp() {
        state = new State(1);
    }

    @Test
    public void newGameConstructorWorksCorrectly() {
        assertEquals(1, state.getCurrentPlayer().getId());
        assertEquals(2, state.getWaitingPlayer().getId());
        
        assertEquals(10, state.getCurrentPlayer().getHand().size());
        assertEquals(1, state.getDiscardPile().size());
        assertEquals(31, state.getDeckSize());
        assertTrue(state.getMelds().isEmpty());
        assertEquals("draw", state.getPhase());
    }
    
    @Test
    public void newRoundConstructorChangesCurrentPlayerAndPhase() {
        state.setPhase("end");
        State newState = state.startNewRound();
        
        assertEquals(state.getWaitingPlayer().getId(), newState.getCurrentPlayer().getId());
        assertEquals(state.getCurrentPlayer().getId(), newState.getWaitingPlayer().getId());
        assertEquals("draw", newState.getPhase());
    }
    
    @Test
    public void newRoundConstructorResetsHandsButNotPoints() {
        state.discardCardNumber(1);
        state.discardCardNumber(1);
        
        state.getCurrentPlayer().setPoints(50);
        
        assertEquals(8, state.getCurrentPlayer().getHand().size());
        assertEquals(50, state.getCurrentPlayer().getPoints());
        
        State newState = state.startNewRound();
        
        assertEquals(10, newState.getWaitingPlayer().getHand().size());
        assertEquals(50, newState.getWaitingPlayer().getPoints());
    }
    
    @Test
    public void drawFromDeckWorksCorrectly() {
        Card draw = state.getDeck()[0];
        assertFalse(state.getCurrentPlayer().getHand().contains(draw));
        state.drawFromDeck();
        assertEquals(30, state.getDeckSize());
        assertFalse(TestUtil.deckContainsCard(state.getDeck(), draw));
        assertTrue(state.getCurrentPlayer().getHand().contains(draw));
        
        assertEquals("meld", state.getPhase());
    }
    
    @Test
    public void drawFromDiscardPileWorksCorrectly() {
        Card draw = state.getDiscardPileTop();
        assertFalse(state.getCurrentPlayer().getHand().contains(draw));
        state.drawFromDiscardPile();
        assertTrue(state.getDiscardPile().isEmpty());
        assertTrue(state.getCurrentPlayer().getHand().contains(draw));
        
        assertEquals("meld", state.getPhase());
    }
    
    @Test
    public void refillDeckWorksCorrectly() {
        state.setDeck(TestUtil.generateRandomDeck(1));
        state.setDiscardPile(TestUtil.generateRandomDiscardPile(30));
        
        assertEquals(1, state.getDeckSize());
        assertEquals(30, state.getDiscardPile().size());
        Card pileTop = state.getDiscardPileTop();
        
        state.drawFromDeck();
        
        assertEquals(29, state.getDeckSize());
        assertEquals(pileTop, state.getDiscardPileTop());
        assertEquals(1, state.getDiscardPile().size());
    }
    
    @Test
    public void findPossibleMeldsWorksCorrectly() {
        List<Meld> expectedMelds = this.createExpectedMelds(state.getCurrentPlayer());
        List<Meld> melds = state.findPossibleMelds();
        
        assertTrue(melds.containsAll(expectedMelds));
        assertTrue(expectedMelds.containsAll(melds));
        
        this.createMeldFreeHand(state.getCurrentPlayer());
        melds = state.findPossibleMelds();
        
        assertTrue(melds.isEmpty());
    }
    
    @Test
    public void meldingWorksCorrectly() {
        List<Meld> melds = this.createExpectedMelds(state.getCurrentPlayer());
        Meld chosenMeld = melds.get(0);
        List<Card> hand = state.getCurrentPlayer().getHand();
        int initHandSize = hand.size();
        
        for (Card card : chosenMeld.getCards()) {
            assertTrue(hand.contains(card));
        }
        
        state.meld(chosenMeld);
        
        hand = state.getCurrentPlayer().getHand();
        
        assertEquals(1, state.getMelds().size());
        assertEquals(chosenMeld, state.getMelds().get(0));
        assertEquals(initHandSize - chosenMeld.getCards().size(), hand.size());
        
        for (Card card : chosenMeld.getCards()) {
            assertFalse(hand.contains(card));
        }
        
        assertEquals("layoff", state.getPhase());
    }
    
    @Test
    public void findPossibleLayoffsWorksCorrectly() {
        List<Meld> melds = this.createMeldsAndLayoffableHand(state.getCurrentPlayer());
        
        state.setMelds(melds);
        
        List<Card> hand = state.getCurrentPlayer().getHand();
        
        List<Layoff> layoffs = state.findPossibleLayoffs();
        
        assertEquals(3, layoffs.size());
        
        boolean fourOfSpadesRun = false;
        boolean fourOfSpadesSet = false;
        boolean eightOfSpadesRun = false;
        
        for (Layoff layoff : layoffs) {
            Card card = layoff.getCard();
            if (card.equals(hand.get(2))) {
                if (layoff.getMeld().getCards().containsAll(melds.get(0).getCards())) {
                    fourOfSpadesRun = true;
                } else if (layoff.getMeld().getCards().containsAll(melds.get(1).getCards())) {
                    fourOfSpadesSet = true;
                }
            } else if (card.equals(hand.get(3))) {
                if (layoff.getMeld().getCards().containsAll(melds.get(0).getCards())) {
                    eightOfSpadesRun = true;
                }
            }
        }
        
        assertTrue(fourOfSpadesRun);
        assertTrue(fourOfSpadesSet);
        assertTrue(eightOfSpadesRun);
    }
    
    @Test
    public void layoffWorksCorrectly1() {
        List<Meld> melds = this.createMeldsAndLayoffableHand(state.getCurrentPlayer());
        this.state.setMelds(melds);
        List<Card> hand = state.getCurrentPlayer().getHand();
        int initHandSize = hand.size();
        
        Layoff fourOfSpadesRun = new Layoff(hand.get(2), melds.get(0));
        Layoff fourOfSpadesSet = new Layoff(hand.get(2), melds.get(1));
        Layoff eightOfSpadesRun = new Layoff(hand.get(3), melds.get(0));
        
        assertFalse(stateMeldsContainCard(fourOfSpadesRun.getCard()));
        assertFalse(stateMeldsContainCard(eightOfSpadesRun.getCard()));

        state.layoff(fourOfSpadesRun, false);
        
        assertFalse(hand.contains(fourOfSpadesRun.getCard()));
        assertFalse(hand.contains(fourOfSpadesSet.getCard()));
        assertTrue(hand.contains(eightOfSpadesRun.getCard()));
        
        assertTrue(stateMeldsContainCard(fourOfSpadesRun.getCard()));
        assertFalse(stateMeldsContainCard(eightOfSpadesRun.getCard()));
        
        state.layoff(eightOfSpadesRun, false);
        
        assertFalse(hand.contains(eightOfSpadesRun.getCard()));
        assertTrue(stateMeldsContainCard(eightOfSpadesRun.getCard()));
        
        assertEquals(initHandSize - 2, hand.size());
        
        state.layoffDone();
        assertEquals("discard", state.getPhase());
    }
    
    @Test
    public void layoffWorksCorrectly2() {
        List<Meld> melds = this.createMeldsAndLayoffableHand(state.getCurrentPlayer());
        this.state.setMelds(melds);
        List<Card> hand = state.getCurrentPlayer().getHand();
        int initHandSize = hand.size();
        
        Layoff fourOfSpadesRun = new Layoff(hand.get(2), melds.get(0));
        Layoff fourOfSpadesSet = new Layoff(hand.get(2), melds.get(1));
        Layoff eightOfSpadesRun = new Layoff(hand.get(3), melds.get(0));
        
        assertFalse(stateMeldsContainCard(fourOfSpadesRun.getCard()));
        assertFalse(stateMeldsContainCard(eightOfSpadesRun.getCard()));
        
        state.layoff(fourOfSpadesSet, false);
        
        assertFalse(hand.contains(fourOfSpadesRun.getCard()));
        assertFalse(hand.contains(fourOfSpadesSet.getCard()));
        assertTrue(hand.contains(eightOfSpadesRun.getCard()));
        
        assertTrue(stateMeldsContainCard(fourOfSpadesSet.getCard()));
        assertFalse(stateMeldsContainCard(eightOfSpadesRun.getCard()));
        
        state.layoff(eightOfSpadesRun, false);
        
        assertFalse(hand.contains(eightOfSpadesRun.getCard()));
        assertTrue(stateMeldsContainCard(eightOfSpadesRun.getCard()));
        
        assertEquals(initHandSize - 2, hand.size());
    }
    
    @Test
    public void discardWorksCorrectly() {
        state.getCurrentPlayer().setHand(TestUtil.generateRandomHand(10));
        state.getCurrentPlayer().organizeHand();
        List<Card> hand = state.getCurrentPlayer().getHand();
        
        Random random = new Random();
        int randomIndex = random.nextInt(hand.size());
        Card randomCard = hand.get(randomIndex);
        state.setPhase("discard");
        
        state.discardCardNumber(randomIndex);
        
        assertFalse(hand.contains(randomCard));
        assertEquals(randomCard, state.getDiscardPileTop());
        assertEquals("end", state.getPhase());
    }
    
    @Test
    public void endTurnChangesPlayersAndPhase() {
        Player current = state.getCurrentPlayer();
        Player waiting = state.getWaitingPlayer();
        state.setPhase("discard");
        
        state.endTurn();
        
        assertEquals(waiting.getId(), state.getCurrentPlayer().getId());
        assertEquals(current.getId(), state.getWaitingPlayer().getId());
        assertEquals("draw", state.getPhase());
    }
    
    @Test
    public void winnerPointsAreUpdatedCorrectly() {
        Player winner = state.getCurrentPlayer();
        Player loser = state.getWaitingPlayer();
        
        winner.setHand(new ArrayList<>());
        ArrayList<Card> hand = new ArrayList<>();
        
        hand.add(new Card(Suit.CLUBS, 6));
        hand.add(new Card(Suit.CLUBS, 8));
        hand.add(new Card(Suit.DIAMONDS, 13));
        loser.setHand(hand);
        
        winner.setPoints(50);
        loser.setPoints(0);
        
        state.updateWinnerPoints();
        
        assertEquals(74, winner.getPoints());
        assertEquals(0, loser.getPoints());
    }
    
    
    //
    // TESTS FOR "THE AI METHODS"
    //
    
    @Test
    public void getAvailableMovesGetsCorrectMoves() {
        List<Meld> melds = this.createMeldsAndLayoffableHand(this.state.getCurrentPlayer());
        this.state.setMelds(melds);
        
        // Draw
        
        Move deckDrawMove = new DrawMove(this.state.getCurrentPlayer(), true);
        Move discardDrawMove = new DrawMove(this.state.getCurrentPlayer(), false);
        
        List<Move> moves = this.state.getAvailableMoves();
        
        assertTrue(moves.contains(deckDrawMove));
        assertTrue(moves.contains(discardDrawMove));
        assertEquals(2, moves.size());
        
        // Meld
        
        this.state.setPhase("meld");
        
        List<Meld> expectedMelds = createExpectedMelds(this.state.getCurrentPlayer());
        
        moves = this.state.getAvailableMoves();
        assertEquals(5, moves.size());
        
        List<Move> expectedMoves = new ArrayList<>();
        
        expectedMoves.add(new MeldMove(this.state.getCurrentPlayer(), expectedMelds.get(0)));
        expectedMoves.add(new MeldMove(this.state.getCurrentPlayer(), expectedMelds.get(1)));
        expectedMoves.add(new MeldMove(this.state.getCurrentPlayer(), expectedMelds.get(2)));
        expectedMoves.add(new MeldMove(this.state.getCurrentPlayer(), expectedMelds.get(3)));
        expectedMoves.add(new PassMove(this.state.getCurrentPlayer(), "meld"));
        
        assertTrue(expectedMoves.containsAll(moves));
        assertTrue(moves.containsAll(expectedMoves));
        
        // Layoff
        
        this.state.setPhase("layoff");
        expectedMelds = createMeldsAndLayoffableHand(this.state.getCurrentPlayer());
        
        moves = this.state.getAvailableMoves();
        List<Card> hand = this.state.getCurrentPlayer().getHand();
        
        expectedMoves = new ArrayList<>();
        expectedMoves.add(new LayoffMove(this.state.getCurrentPlayer(), new Layoff(hand.get(2), expectedMelds.get(0))));
        expectedMoves.add(new LayoffMove(this.state.getCurrentPlayer(), new Layoff(hand.get(3), expectedMelds.get(0))));
        expectedMoves.add(new LayoffMove(this.state.getCurrentPlayer(), new Layoff(hand.get(2), expectedMelds.get(1))));
        expectedMoves.add(new PassMove(this.state.getCurrentPlayer(), "layoff"));
        
        assertEquals(4, moves.size());
        assertTrue(expectedMoves.containsAll(moves));
        assertTrue(moves.containsAll(expectedMoves));
        
        // Discard
        
        this.state.setPhase("discard");
        moves = this.state.getAvailableMoves();
        expectedMoves = new ArrayList<>();
        
        for (Card card : hand) {
            expectedMoves.add(new DiscardMove(this.state.getCurrentPlayer(), card));
        }
        
        assertEquals(hand.size(), moves.size());
        assertTrue(expectedMoves.containsAll(moves));
        assertTrue(moves.containsAll(expectedMoves));
        
        // End
        
        this.state.setPhase("end");
        moves = this.state.getAvailableMoves();
        
        assertEquals(1, moves.size());
        assertEquals(new PassMove(this.state.getCurrentPlayer(), "end"), moves.get(0));
    }
    
    
    
    //
    // HELPER METHODS
    //
    

    
    private List<Meld> createExpectedMelds(Player player) {
        List<Card> hand = new ArrayList<>();
        hand.add(new Card(Suit.CLUBS, 1));
        hand.add(new Card(Suit.CLUBS, 12));
        hand.add(new Card(Suit.SPADES, 1));
        hand.add(new Card(Suit.SPADES, 9));
        hand.add(new Card(Suit.SPADES, 13));
        hand.add(new Card(Suit.DIAMONDS, 9));
        hand.add(new Card(Suit.DIAMONDS, 10));
        hand.add(new Card(Suit.DIAMONDS, 11));
        hand.add(new Card(Suit.DIAMONDS, 12));
        hand.add(new Card(Suit.HEARTS, 8));
        hand.add(new Card(Suit.HEARTS, 12));
        
        player.setHand(hand);
        player.organizeHand();
        
        LinkedList<Card> supposedRunMeld1 = new LinkedList<>();
        supposedRunMeld1.add(hand.get(5));
        supposedRunMeld1.add(hand.get(6));
        supposedRunMeld1.add(hand.get(7));
        supposedRunMeld1.add(hand.get(8));
        
        LinkedList<Card> supposedRunMeld2 = new LinkedList<>();
        supposedRunMeld2.add(hand.get(5));
        supposedRunMeld2.add(hand.get(6));
        supposedRunMeld2.add(hand.get(7));
        
        LinkedList<Card> supposedRunMeld3 = new LinkedList<>();
        supposedRunMeld3.add(hand.get(6));
        supposedRunMeld3.add(hand.get(7));
        supposedRunMeld3.add(hand.get(8));
        
        LinkedList<Card> supposedSetMeld1 = new LinkedList<>();
        supposedSetMeld1.add(hand.get(1));
        supposedSetMeld1.add(hand.get(8));
        supposedSetMeld1.add(hand.get(10));

        List<Meld> melds = new ArrayList<>();
        melds.add(new RunMeld(player, supposedRunMeld1));
        melds.add(new RunMeld(player, supposedRunMeld2));
        melds.add(new RunMeld(player, supposedRunMeld3));
        melds.add(new SetMeld(player, supposedSetMeld1));
        
        return melds;
    }
    
    private void createMeldFreeHand(Player player) {
        List<Card> hand = new ArrayList<>();
        hand.add(new Card(Suit.CLUBS, 1));
        hand.add(new Card(Suit.CLUBS, 2));
        hand.add(new Card(Suit.CLUBS, 5));
        hand.add(new Card(Suit.CLUBS, 13));
        hand.add(new Card(Suit.SPADES, 1));
        hand.add(new Card(Suit.SPADES, 2));
        hand.add(new Card(Suit.SPADES, 13));
        hand.add(new Card(Suit.DIAMONDS, 3));
        hand.add(new Card(Suit.DIAMONDS, 4));
        hand.add(new Card(Suit.DIAMONDS, 11));
        hand.add(new Card(Suit.HEARTS, 12));
        
        player.setHand(hand);
        player.organizeHand();
    }
    
    private List<Meld> createMeldsAndLayoffableHand(Player player) {
        List<Card> hand = new ArrayList<>();
        hand.add(new Card(Suit.CLUBS, 1));
        hand.add(new Card(Suit.CLUBS, 12));
        hand.add(new Card(Suit.SPADES, 4));
        hand.add(new Card(Suit.SPADES, 8));
        hand.add(new Card(Suit.SPADES, 13));
        hand.add(new Card(Suit.DIAMONDS, 9));
        hand.add(new Card(Suit.HEARTS, 8));
        
        player.setHand(hand);
        player.organizeHand();
        
        LinkedList<Card> meldCards1 = new LinkedList<>();
        meldCards1.add(new Card(Suit.SPADES, 5));
        meldCards1.add(new Card(Suit.SPADES, 6));
        meldCards1.add(new Card(Suit.SPADES, 7));
        
        LinkedList<Card> meldCards2 = new LinkedList<>();
        meldCards2.add(new Card(Suit.HEARTS, 4));
        meldCards2.add(new Card(Suit.DIAMONDS, 4));
        meldCards2.add(new Card(Suit.CLUBS, 4));
        
        List<Meld> melds = new ArrayList<>();
        melds.add(new RunMeld(player, meldCards1));
        melds.add(new SetMeld(player, meldCards2));
        
        return melds;
    }
    
    private boolean stateMeldsContainCard(Card card) {
        for (Meld meld : this.state.getMelds()) {
            if (meld.getCards().contains(card)) {
                return true;
            }
        }
        return false;
    }
}