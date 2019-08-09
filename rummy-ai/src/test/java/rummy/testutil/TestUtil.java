package rummy.testutil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import rummy.game.domain.Card;
import rummy.game.domain.State;
import rummy.game.domain.Suit;
import rummy.game.domain.move.Move;


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
    
    public static List<Move> getAvailableMoves() {
        State state = new State(1);
        return state.getAvailableMoves();
    }
}
