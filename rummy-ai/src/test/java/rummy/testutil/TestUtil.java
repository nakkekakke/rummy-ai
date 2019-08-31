package rummy.testutil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import rummy.ai.Node;
import rummy.game.domain.Card;
import rummy.game.domain.State;
import rummy.game.domain.Suit;
import rummy.game.domain.meld.Meld;
import rummy.game.domain.meld.RunMeld;
import rummy.game.domain.move.DrawMove;
import rummy.game.domain.move.MeldMove;
import rummy.game.domain.move.Move;
import rummy.game.domain.move.PassMove;
import rummy.ai.AIArrayList;


public class TestUtil {
    
    
    public static boolean deckContainsCard(Card[] deck, Card card) {
        for (Card deckCard : deck) {
            if (deckCard.equals(card)) {
                return true;
            }
        }
        return false;
    }
    
    public static Card[] generateRandomDeck(int n) {
        Random random = new Random();
        Card[] cards = new Card[n];
        for (int i = 0; i < n; i++) {
            int rank = 1 + random.nextInt(13);
            int suitInt = 1 + random.nextInt(4);
            
            Suit suit;
            switch (suitInt) {
                case 1:
                    suit = Suit.CLUBS;
                    break;
                case 2:
                    suit = Suit.SPADES;
                    break;
                case 3:
                    suit = Suit.DIAMONDS;
                    break;
                default:
                    suit = Suit.HEARTS;
                    break;
            }
            
            cards[i] = new Card(suit, rank);
        }
        
        return cards;
    }
    
    public static Stack<Card> generateRandomDiscardPile(int n) {
        Random random = new Random();
        Stack<Card> cards = new Stack<>();
        for (int i = 0; i < n; i++) {
            int rank = 1 + random.nextInt(13);
            int suitInt = 1 + random.nextInt(4);
            
            Suit suit;
            switch (suitInt) {
                case 1:
                    suit = Suit.CLUBS;
                    break;
                case 2:
                    suit = Suit.SPADES;
                    break;
                case 3:
                    suit = Suit.DIAMONDS;
                    break;
                default:
                    suit = Suit.HEARTS;
                    break;
            }
            
            cards.push(new Card(suit, rank));
        }
        
        return cards;
    }
    
    public static List<Card> generateRandomHand(int n) {
        Random random = new Random();
        List<Card> hand = new ArrayList<>();
        
        for (int i = 0; i < n; i++) {
            int rank = 1 + random.nextInt(13);
            int suitInt = 1 + random.nextInt(4);
            
            Suit suit;
            switch (suitInt) {
                case 1:
                    suit = Suit.CLUBS;
                    break;
                case 2:
                    suit = Suit.SPADES;
                    break;
                case 3:
                    suit = Suit.DIAMONDS;
                    break;
                default:
                    suit = Suit.HEARTS;
                    break;
            }
            Card newCard = new Card(suit, rank);
            
            if (!hand.contains(newCard)) {
                hand.add(new Card(suit, rank));
            } else {
                i--;
            }
            
        }
        return hand;
    }
    
    public static LinkedList<Card> generateRunMeldCards(int from, int to, Suit suit) {
        LinkedList<Card> meld = new LinkedList<>();
        
        for (int i = from; i <= to; i++) {
            meld.add(new Card(suit, i));
        }
        
        return meld;
    }
    
    public static LinkedList<Card> generateSetMeldCards(int rank, int size) {
        LinkedList<Card> meld = new LinkedList<>();
        
        meld.add(new Card(Suit.SPADES, rank));
        meld.add(new Card(Suit.CLUBS, rank));
        meld.add(new Card(Suit.DIAMONDS, rank));
        
        if (size == 4) {
            meld.add(new Card(Suit.HEARTS, rank));
        }
        
        return meld;
    }
    
    public static AIArrayList<Move> getAvailableMoves() {
        State state = new State(1);
        return state.getAvailableMoves();
    }
    
    public static AIArrayList<Move> getAvailableMoves(String phase) {
        State state = new State(1);
        state.setPhase(phase);
        
        if (phase.equals("meld")) {
            state.getCurrentPlayer().setHand(TestUtil.generateRunMeldCards(4, 10, Suit.CLUBS));
            state.getCurrentPlayer().addToHand(new Card(Suit.HEARTS, 11));
            state.getCurrentPlayer().addToHand(new Card(Suit.CLUBS, 11));
        }
        return state.getAvailableMoves();
    }
    
    public static Node createSearchTree(Node root) {
        Node child1 = root.addChild(new DrawMove(root.getPlayer(), true), root.getPlayer());
        Node child2 = root.addChild(new DrawMove(root.getPlayer(), false), root.getPlayer());
        
        root.setVisits(20);
        root.setTotalScore(5.53);
        
        child1.setTotalScore(3.0);
        child1.setVisits(5);
        child1.setConsiderations(10);
        
        child2.setTotalScore(2.53);
        child2.setVisits(5);
        child2.setConsiderations(10);
        
        root.getPlayer().setHand(TestUtil.generateRunMeldCards(4, 10, Suit.CLUBS));
        root.getPlayer().addToHand(new Card(Suit.CLUBS, 11));
        Meld meld = new RunMeld(root.getPlayer(), TestUtil.generateRunMeldCards(4, 10, Suit.CLUBS));
        
        Node meld1Child1 = child1.addChild(new MeldMove(root.getPlayer(), meld), root.getPlayer());
        Node meld1Child2 = child2.addChild(new MeldMove(root.getPlayer(), meld), root.getPlayer());
        
        meld1Child1.setTotalScore(2.1);
        meld1Child1.setVisits(3);
        meld1Child1.setConsiderations(5);
        
        meld1Child2.setTotalScore(1.73);
        meld1Child2.setVisits(3);
        meld1Child2.setConsiderations(5);
        
        Node meld2Child1 = child1.addChild(new PassMove(root.getPlayer(), "meld"), root.getPlayer());
        Node meld2Child2 = child2.addChild(new PassMove(root.getPlayer(), "meld"), root.getPlayer());
        
        meld2Child1.setTotalScore(0.9);
        meld2Child1.setVisits(2);
        meld2Child1.setConsiderations(5);
        
        meld2Child2.setTotalScore(0.8);
        meld2Child2.setVisits(2);
        meld2Child2.setConsiderations(5);
        
        return root;
    }
}
