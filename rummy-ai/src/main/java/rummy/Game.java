package rummy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;


public final class Game {
    
    private State state;
    private static final int DECK_SIZE = 52;
    private static final int HAND_SIZE = 10;
    private static final String[] meldTypes = {"SET", "RUN"};
    private final Logger log;
    
    public Game(int startingPlayer, Logger log) {
        this.log = log;
        this.state = getFreshState(startingPlayer);
        initializeHands();
        
    }
    
    public State getState() {
        return this.state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public State getFreshState(int startingPlayer) {
        if (startingPlayer == 1) {
            log.debug("Game initialized with player 1 starting");
            return new State(new Player(1), new Player(2), createDeck(), new Stack<>(), new ArrayList<>());
        } else {
            log.debug("Game initialized with player 2 starting");
            return new State(new Player(2), new Player(1), createDeck(), new Stack<>(), new ArrayList<>()); 
        }
    }
    
    public final Card[] createDeck() {
        Card[] deck = new Card[DECK_SIZE];
        
        int i = 0;
        for (Suit suit : Suit.values()) {
            for (int rank = 1; rank <= 13; rank++) {
                deck[i] = (new Card(suit, rank));
                i++;
                log.debug("Card " + suit + "-" + rank + " created");
            }
        }
        
        return shuffleDeck(deck);
    }
    
    private Card[] shuffleDeck(Card[] deck) {
        
        Random random = new Random();
        for (int i = deck.length - 1; i > 0; i--) {
            int randomIndex = random.nextInt(i + 1);
            Card helper = deck[randomIndex];
            deck[randomIndex] = deck[i];
            deck[i] = helper;
        }
        
        return deck;
    }
    
    private void initializeHands() {
        Card[] deck = this.state.getDeck();
        for (int i = 0; i < HAND_SIZE * 2; i += 2) {
            this.state.getCurrentPlayer().addToHand(deck[i]);
            this.state.getWaitingPlayer().addToHand(deck[i + 1]);
            log.debug("Dealt " + deck[i] + " for player " + this.state.getCurrentPlayer().getId());
            log.debug("Dealt " + deck[i + 1] + " for player " + this.state.getWaitingPlayer().getId());
        }
        
        this.state.getCurrentPlayer().organizeHand();
        this.state.getWaitingPlayer().organizeHand();
        
        Card[] newDeck = new Card[deck.length - 20];
        for (int i = 0; i < newDeck.length; i++) {
            newDeck[i] = deck[i + 20];
        }
        
        this.state.setDeck(newDeck);
    }
    
    public int getCurrentPlayerId() {
        return this.state.getCurrentPlayer().getId();
    }
    
    public List<Card> getPlayerHand() {
        return this.state.getCurrentPlayer().getHand();
    }
    
    public Card drawFromDeck() {
        Card[] oldDeck = this.state.getDeck();
        Card draw = oldDeck[0];
        Card[] newDeck = shortenDeckByOne(oldDeck);
        
        this.state.setDeck(newDeck);
        this.state.getCurrentPlayer().addToHand(draw);
        return draw;
    }
    
    public void initialDraw() {
        Card[] oldDeck = this.state.getDeck();
        Card draw = oldDeck[0];
        Card[] newDeck = shortenDeckByOne(oldDeck);
        
        this.state.setDeck(newDeck);
        this.state.getDiscardPile().push(draw);
    }
    
    private Card[] shortenDeckByOne(Card[] deck) {
        Card[] newDeck = new Card[deck.length - 1];
        for (int i = 0; i < newDeck.length; i++) {
            newDeck[i] = deck[i + 1];
        }
        return newDeck;
    }
    
    public Card getDiscardPileTop() {
        return this.state.getDiscardPile().peek();
    }
    
    public int getDeckSize() {
        return this.state.getDeck().length;
    }
    
    public int getHandSize(boolean own) {
        if (own) {
            return this.state.getCurrentPlayer().getHand().size();
        }
        
        return this.state.getWaitingPlayer().getHand().size();
    }
    
    public void organizeCurrentHand() {
        this.state.getCurrentPlayer().organizeHand();
    }
    
    public Card drawFromDiscardPile() {
        Card draw = this.state.getDiscardPile().pop();
//        Card[] newDiscardPile = new Card[oldDiscardPile.length - 1];
//        for (int i = 0; i < newDiscardPile.length; i++) {
//            newDiscardPile[i] = oldDiscardPile[i + 1];
//        }
//        
//        this.state.setDiscardPile(newDiscardPile);
        this.state.getCurrentPlayer().addToHand(draw);
        return draw;
    }
    
    public void discard(Card card) {
        this.state.getDiscardPile().push(card);
//        Card[] newDiscardPile = new Card[oldDiscardPile.length + 1];
//        newDiscardPile[0] = card;
//        for (int i = 0; i < oldDiscardPile.length; i++) {
//            newDiscardPile[i + 1] = oldDiscardPile[i];
//        }
//        
//        this.state.setDiscardPile(newDiscardPile);
        this.state.getCurrentPlayer().discard(card);
    }
    
    public Card discardCardNumber(int number) {
        Card card = this.state.getCurrentPlayer().getHand().get(number);
        discard(card);
        return card;
    }
    
    // assumes that hand cards are in order !!!
    public List<Meld> findPossibleMelds() {
        List<Meld> possibleMelds = new ArrayList<>();
        List<Card> hand = this.state.getCurrentPlayer().getHand();
        
        int[][] ranks = new int[13][5]; // first column is rank count (index is rank number), 
                                        // other 4 columns are for card indices in hand (indices are +1 so 0 means that card isnt in hand) for each possible rank card
        
        // (RUN) MAIN LOOP, represents the lower index
        for (int i = 0; i < hand.size(); i++) {
            
            // count ranks while searching for run melds for extra efficiency
            ranks[hand.get(i).getValue() - 1][0]++; 
            for (int x = 1; x < 5; x++) {
                if (ranks[hand.get(i).getValue() - 1][x] == 0) {
                    ranks[hand.get(i).getValue() - 1][x] = i + 1;
                    break;
                } 
            }
            
            // (RUN) SECONDARY LOOP, represents the higher index. Finds all possible run melds of any length (>= 3) between the two indices
            for (int j = i + 1; j < hand.size(); j++) {
                
                if ( (hand.get(i).getSuit() == hand.get(j).getSuit()) && (hand.get(i).getValue() == hand.get(j).getValue() - (j - i)) ) {
                    if (j - i >= 2) {
                        LinkedList<Card> meldCards = new LinkedList<>();
                        for (int k = i; k <= j; k++) {
                            meldCards.addLast(hand.get(k));
                        }
                        Meld possibleMeld = new RunMeld(this.state.getCurrentPlayer(), meldCards);
                        possibleMelds.add(possibleMeld);
                    }
                } else if (j - i < 2) {
                    i = j - 1;
                    break;
                } else {
                    break;
                }
            }
        }
        
        log.debug("Meld array:");
        for (int[] rank : ranks) {
            for(int x : rank) {
                log.print(Integer.toString(x));
            }
            log.print("NEXT");
        }
        
        // All run melds found, now find all set melds using rank data gathered earlier
        
        // (SET) MAIN LOOP
        for (int i = 0; i < 13; i++) {
            
            // Create a 3-long meld when you have 3 of the same rank in hand (simple)
            if (ranks[i][0] == 3) {
            
                LinkedList<Card> meldCards = new LinkedList<>();
                for (int j = 1; j < 5; j++) {
                    if (ranks[i][j] != 0) {
                        meldCards.add(hand.get(ranks[i][j] - 1));
                    }
                }
                possibleMelds.add(new SetMeld(this.state.getCurrentPlayer(), meldCards));
            
            // Create one 4-long meld and every possible 3-long meld when you have 4 of the same rank in hand (first skip 1st card, then second etc)
            } else if (ranks[i][0] == 4) {
                
                // Loop through all 5 indices in one ranks[][] row rank[5], skipping the first value (count). Also one extra loop to create one 4-long meld.
                for (int j = 1; j < 6; j++) {
                    
                    LinkedList<Card> meldCards = new LinkedList<>();
                    for (int k = 1; k < 5; k++) {
                        if (j != k) {
                            meldCards.add(hand.get(ranks[i][k] - 1));
                        }
                    }
                    possibleMelds.add(new SetMeld(this.state.getCurrentPlayer(), meldCards));
                }
            }
        }
        
        return possibleMelds;
    }
    
//    private void findPossibleRuns(Card card) {
//        
//    }
//    
//    private void findPossibleSets(Card card) {
//        
//    }
    
    public Meld meld(Meld meld) {
        for (int i = 0; i < meld.getCards().size(); i++) {
            this.state.getCurrentPlayer().discard(meld.getCards().get(i));
        }
        this.state.getMelds().add(meld);
        return meld;
    }
    
    public List<Meld> getCurrentMelds() {
        return this.state.getMelds();
    }
    
    public List<Layoff> findPossibleLayoffs() {
        List<Layoff> layoffs = new ArrayList<>();
        List<Card> hand = this.state.getCurrentPlayer().getHand();
        List<Meld> melds = this.state.getMelds();
        for (int i = 0; i < hand.size(); i++) {
            for (int j = 0; j < melds.size(); j++) {
                if (melds.get(j).layOffAllowed(hand.get(i))) {
                    layoffs.add(new Layoff(hand.get(i), melds.get(j)));
                }
            }
        }
        return layoffs;
    }
    
    public Layoff layoff(Layoff layoff) {
        this.state.getCurrentPlayer().discard(layoff.getCard());
        layoff.getMeld().layoff(layoff.getCard());
        return layoff;
    }
    
    public void endTurn() {
        Player current = this.state.getCurrentPlayer();
        this.state.setCurrentPlayer(this.state.getWaitingPlayer());
        this.state.setWaitingPlayer(current);
    }
    
    public boolean gameOver() {
        return this.state.getCurrentPlayer().getHand().isEmpty();
    }
    
    
}
