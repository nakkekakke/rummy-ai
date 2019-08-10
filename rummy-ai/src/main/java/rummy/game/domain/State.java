package rummy.game.domain;

import rummy.game.domain.Suit;
import rummy.game.util.Logger;
import rummy.game.domain.meld.SetMeld;
import rummy.game.domain.meld.RunMeld;
import rummy.game.domain.meld.Meld;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import rummy.game.domain.move.DiscardMove;
import rummy.game.domain.move.DrawMove;
import rummy.game.domain.move.LayoffMove;
import rummy.game.domain.move.MeldMove;
import rummy.game.domain.move.Move;

public class State {
    
    private static final int DECK_SIZE = 52;
    private static final int MAX_HAND_SIZE = 10;
    private static final int PLAYER_COUNT = 2;
    private static final String[] phases = {"draw", "meld", "layoff", "discard", "end"};
    private final Logger log = new Logger(false);
    private Player currentPlayer;
    private Player waitingPlayer;
    private Card[] deck;
    private Stack<Card> discardPile;
    private List<Meld> melds;
    private String phase;
    private Meld currentMeld; // meld used this turn
    private Card[][] knownHandCards;
    private Player loser;
    
    // for starting a new game
    public State(int currentPlayerId) {
        this.currentPlayer = new Player(currentPlayerId);
        this.waitingPlayer = new Player((currentPlayerId % PLAYER_COUNT) + 1);
        this.discardPile = new Stack<>();
        this.melds = new ArrayList<>();
        this.phase = "draw";
        this.currentMeld = null;
        this.knownHandCards = new Card[PLAYER_COUNT][MAX_HAND_SIZE + 1];
        this.loser = null;
        this.deal();
    }
    
    // for starting a new round
    public State(Player currentPlayer, Player waitingPlayer) {
        this.currentPlayer = currentPlayer;
        this.waitingPlayer = waitingPlayer;
        this.discardPile = new Stack<>();
        this.melds = new ArrayList<>();
        this.phase = "draw";
        this.currentMeld = null;
        this.knownHandCards = new Card[PLAYER_COUNT][MAX_HAND_SIZE + 1];
        this.loser = null;
        this.deal();
    }
    
    // for cloning
    public State(Player currentPlayer, Player waitingPlayer, Card[] deck, Stack<Card> discardPile, List<Meld> melds, String phase, Card[][] knownHandCards, Meld currentMeld) {
        this.currentPlayer = currentPlayer;
        this.waitingPlayer = waitingPlayer;
        this.deck = deck;
        this.discardPile = discardPile;
        this.melds = melds;
        this.phase = phase;
        this.knownHandCards = knownHandCards;
        this.currentMeld = currentMeld;
        this.loser = null;
    }
    
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
    
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
    
    public Player getWaitingPlayer() {
        return this.waitingPlayer;
    }
    
    public void setWaitingPlayer(Player player) {
        this.waitingPlayer = player;
    }
    
    public Card[] getDeck() {
        return this.deck;
    }
    
    public void setDeck(Card[] deck) {
        this.deck = deck;
    }
    
    public Stack<Card> getDiscardPile() {
        return this.discardPile;
    }
    
    public void setDiscardPile(Stack<Card> discardPile) {
        this.discardPile = discardPile;
    }
    
    public List<Meld> getMelds() {
        return this.melds;
    }
    
    public void setMelds(List<Meld> melds) {
        this.melds = melds;
    }
    
    public String getPhase() {
        return this.phase;
    }
    
    public void setPhase(String phase) {
        this.phase = phase;
    }
    
    public Card getDiscardPileTop() {
        return this.discardPile.peek();
    }
    
    public int getDeckSize() {
        return this.deck.length;
    }
    
    public int getOpponentHandSize() {
        return this.waitingPlayer.getHand().size();
    }
    
    public Card[][] getKnownHandCards() {
        return this.knownHandCards;
    }
    
    private void deal() {
        this.deck = shuffleDeck(createDeck());
        initializeHands();
        initialDraw();
    }
    
    public final Card[] createDeck() {
        Card[] newDeck = new Card[DECK_SIZE];
        
        int i = 0;
        for (Suit suit : Suit.values()) {
            for (int rank = 1; rank <= 13; rank++) {
                newDeck[i] = (new Card(suit, rank));
                i++;
                log.debug("Card " + suit + "-" + rank + " created");
            }
        }
        
        return newDeck;
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
        this.currentPlayer.setHand(new ArrayList<>());
        this.waitingPlayer.setHand(new ArrayList<>());
        for (int i = 0; i < MAX_HAND_SIZE * 2; i += 2) {
            this.currentPlayer.addToHand(this.deck[i]);
            this.waitingPlayer.addToHand(this.deck[i + 1]);
            log.debug("Dealt " + this.deck[i] + " for player " + this.currentPlayer.getId());
            log.debug("Dealt " + this.deck[i + 1] + " for player " + this.waitingPlayer.getId());
        }
        
        this.currentPlayer.organizeHand();
        this.waitingPlayer.organizeHand();
        
        Card[] newDeck = new Card[this.deck.length - 20];
        for (int i = 0; i < newDeck.length; i++) {
            newDeck[i] = this.deck[i + 20];
        }
        
        this.deck = newDeck;
    }
    
    public void initialDraw() {
        Card draw = this.deck[0];
        Card[] newDeck = shortenDeckByN(this.deck, 1);
        
        this.deck = newDeck;
        this.discardPile.push(draw);
    }
    
    public Card drawFromDeck() {
        Card draw = this.deck[0];
        Card[] newDeck = shortenDeckByN(this.deck, 1);
        this.deck = newDeck;
        
        if (newDeck.length == 0) {
            this.deck = this.refillDeckFromDiscardPile();
            System.out.println("Deck refilled");
        }
        
        this.currentPlayer.addToHand(draw);
        this.phase = "meld";
        return draw;
    }
    
    private Card[] shortenDeckByN(Card[] deck, int n) {
        Card[] newDeck = new Card[deck.length - n];
        for (int i = 0; i < newDeck.length; i++) {
            newDeck[i] = deck[i + n];
        }
        return newDeck;
    }
    
    private Card[] refillDeckFromDiscardPile() {
        Card discardTop = this.discardPile.pop();
        Card[] newDeck = new Card[this.discardPile.size()];
        
        for (int i = 0; i < newDeck.length; i++) {
            newDeck[i] = this.discardPile.pop();
        }
        
        this.discardPile.push(discardTop);
        return shuffleDeck(newDeck);
    }
    
    public void organizeCurrentHand() {
        this.currentPlayer.organizeHand();
    }
    
    public Card drawFromDiscardPile() {
        Card draw = this.discardPile.pop();
        this.currentPlayer.addToHand(draw);
        this.phase = "meld";
        addToKnownHandCards(draw, this.currentPlayer);
        return draw;
    }
    
    // assumes that hand cards are in order !!!
    public List<Meld> findPossibleMelds() {
        List<Meld> possibleMelds = new ArrayList<>();
        List<Card> hand = this.currentPlayer.getHand();
        
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
                        Meld possibleMeld = new RunMeld(this.currentPlayer, meldCards);
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
        
//        log.debug("Meld array:");
//        for (int[] rank : ranks) {
//            for(int x : rank) {
//                log.print(Integer.toString(x));
//            }
//            log.print("NEXT");
//        }
        
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
                possibleMelds.add(new SetMeld(this.currentPlayer, meldCards));
            
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
                    possibleMelds.add(new SetMeld(this.currentPlayer, meldCards));
                }
            }
        }
        
        return possibleMelds;
    }
    
    public Meld meld(Meld meld) {
        for (int i = 0; i < meld.getCards().size(); i++) {
            this.currentPlayer.discard(meld.getCards().get(i));
        }
        this.melds.add(meld);
        this.currentMeld = meld;
        this.phase = "layoff";
        return meld;
    }
    
    public List<Layoff> findPossibleLayoffs() {
        List<Layoff> layoffs = new ArrayList<>();
        List<Card> hand = this.currentPlayer.getHand();
        for (int i = 0; i < hand.size(); i++) {
            for (int j = 0; j < this.melds.size(); j++) {
                if (this.melds.get(j).layOffAllowed(hand.get(i)) && !this.melds.get(j).equals(this.currentMeld)) {
                    layoffs.add(new Layoff(hand.get(i), this.melds.get(j)));
                }
            }
        }
        return layoffs;
    }
    
    public void layoff(Layoff layoff) {
        this.currentPlayer.discard(layoff.getCard());
        layoff.getMeld().layoff(layoff.getCard());
    }
    
    public void layoffDone() {
        this.phase = "discard";
    }
    
    public Card discardCardNumber(int number) {
        Card card = this.currentPlayer.getHand().get(number);
        this.discard(card);
        return card;
    }
        
    public void discard(Card card) {
        this.discardPile.push(card);
        this.currentPlayer.discard(card);
        removeFromKnownHandCards(card, this.currentPlayer);
        this.phase = "end";
        this.currentMeld = null;
    }
    
    public void endTurn() {
        Player current = this.currentPlayer;
        this.currentPlayer = this.waitingPlayer;
        this.waitingPlayer = current;
        this.phase = "draw";
    }
     
    public int calculateRoundPoints() {
        int sum = 0;
        for (int i = 0; i < this.waitingPlayer.getHand().size(); i++) {
            sum += this.waitingPlayer.getHand().get(i).getValue();
        }
        return sum;
    }
    
    public void updateWinnerPoints() {
        this.currentPlayer.setPoints(this.currentPlayer.getPoints() + calculateRoundPoints());
    }
    
    public State startNewRound() {
        return new State(this.waitingPlayer, this.currentPlayer); // loser starts the next round
    }
        
    public boolean roundOver() {
        return currentPlayer.getHand().isEmpty();
    }
    
    // remove in the future
    public boolean gameOver() {
        return this.loser != null;
    }
    
    
    
    //
    // "THE AI METHODS"
    //
    
    public List<Move> getAvailableMoves() {
        List<Move> moves = new ArrayList<>();
        switch (this.phase) {
            case "draw":
                moves.add(new DrawMove(this.currentPlayer, this, true));
                moves.add(new DrawMove(this.currentPlayer, this, false));
                break;
            case "meld":
                for (Meld meld : findPossibleMelds()) {
                    moves.add(new MeldMove(this.currentPlayer, this, meld));
                }
                break;
            case "layoff":
                for (Layoff layoff : findPossibleLayoffs()) {
                    moves.add(new LayoffMove(this.currentPlayer, this, layoff));
                }
                break;
            default:
                break;
        }
        
        return moves;
    }
    
    public void doMove(Move move) {
        switch (move.type()) {
            case "draw":
                doDrawMove((DrawMove) move);
                break;
            case "meld":
                doMeldMove((MeldMove) move);
                break;
            case "layoff":
                doLayoffMove((LayoffMove) move);
                break;
            case "discard":
                doDiscardMove((DiscardMove) move);
                break;
            default:
                System.out.println("Oopsie");
                break;
        }
    }
    
    private void doDrawMove(DrawMove move) {
        if (move.isDeckDraw()) {
            drawFromDeck();
        } else {
            drawFromDiscardPile();
        }
    }
    
    private void doMeldMove(MeldMove move) {
        meld(move.getMeld());
    }
    
    private void doLayoffMove(LayoffMove move) {
        layoff(move.getLayoff());
    }
    
    private void doDiscardMove(DiscardMove move) {
        discard(move.getCard());
    }
    
    @SuppressWarnings("unchecked")
    public State cloneState() {
        List<Meld> cloneMelds = new ArrayList<>();
        
        for (int i = 0; i < this.melds.size(); i++) {
            Meld meld = this.melds.get(i);
            Meld cloneMeld;
            
            if (meld.type().equals("run")) {
                cloneMeld = new RunMeld(new Player(meld.getPlayer()), (LinkedList<Card>) meld.getCards().clone());
            } else {
                cloneMeld = new SetMeld(new Player(meld.getPlayer()), (LinkedList<Card>) meld.getCards().clone());
            }
            
            cloneMelds.add(cloneMeld);
        }
        
        return new State(
                new Player(this.currentPlayer), 
                new Player(this.waitingPlayer), 
                this.deck.clone(), 
                (Stack<Card>) this.discardPile.clone(), 
                cloneMelds, 
                this.phase, 
                this.knownHandCards, 
                this.currentMeld
        );
    }
    
    public State cloneAndRandomizeState() {
        State clone = cloneState();
        
        Card[] knownCards = new Card[DECK_SIZE];
        int knownIndex = 0;
        
        // add own hand cards to known cards
        for (Card card : clone.getCurrentPlayer().getHand()) {
            knownCards[knownIndex] = card;
            knownIndex++;
        }
        
        // add discard pile to known cards
        for (Card card : clone.getDiscardPile()) {
            knownCards[knownIndex] = card;
            knownIndex++;
        }
        
        // add all melds to known cards
        for (Meld meld : clone.getMelds()) {
            for (Card card : meld.getCards()) {
                knownCards[knownIndex] = card;
                knownIndex++;
            }
        }
        
        // add known opponent's hand cards to known cards
        for (int j = 0; j < MAX_HAND_SIZE + 1; j++) {
            if (clone.getKnownHandCards()[clone.getWaitingPlayer().getId()][j] != null) {
                knownCards[knownIndex] = clone.getKnownHandCards()[clone.getWaitingPlayer().getId()][j];
                knownIndex++;
            }
        }
        
        int unknownIndex = 0;
        Card[] unknownCards = new Card[DECK_SIZE - knownCards.length];
        Card[] fullDeck = createDeck();
        
        // add every card but the known cards to unknown cards
        for (int i = 0; i < DECK_SIZE; i++) {
            boolean known = false;
            
            for (int j = 0; j < knownCards.length; j++) {
                if (fullDeck[i].equals(knownCards[j])) {
                    known = true;
                    break;
                }
            }
            
            if (!known) {
                unknownCards[unknownIndex] = fullDeck[i];
            }
        }
        
        unknownCards = shuffleDeck(unknownCards);

        Card[] opponentKnownCards = clone.getKnownHandCards()[clone.getWaitingPlayer().getId()];
        ArrayList<Card> opponentHandGuess = new ArrayList<>();
        
        // add cards that we know are in opponent's hand to "opponent's hand guess"
        for (int i = 0; i < opponentKnownCards.length; i++) {
            if (opponentKnownCards[i] != null) {
                opponentHandGuess.add(opponentKnownCards[i]);
            }
        }
        
        // add random cards to "opponent's hand guess" until the size is same as the actual hand
        int i;
        for (i = 0; i < clone.getOpponentHandSize() - opponentHandGuess.size(); i++) {
            opponentHandGuess.add(unknownCards[i]);
        }
        
        unknownCards = shortenDeckByN(unknownCards, i);
        
        // set randomized hidden data to clone state
        clone.getWaitingPlayer().setHand(opponentHandGuess);
        clone.setDeck(unknownCards);
        
        return clone;
    }

    
    // 1 point = 0.505
    // 10 points = 0.55
    // 20 points = 0.6
    // 50 points = 0.75
    // 60 points = 0.8
    // 80 points = 0.9
    // 100 points = win = 1.0
    public double getWinResult() {
        double result = -1.0;
        int points = calculateRoundPoints();
        
        if (roundOver()) {
            if (points >= 100) {
                result = 1;
            } else {
                result = 0.5 + (points / 200.0);
            }
        }
        
        return result;
    }


    private void addToKnownHandCards(Card card, Player player) {
        for (int i = 0; i < MAX_HAND_SIZE + 1; i++) {
            if (this.knownHandCards[player.getId() - 1][i] != null) {
                this.knownHandCards[player.getId() - 1][i] = card;
                break;
            }
        }
    }
    
    private void removeFromKnownHandCards(Card card, Player player) {
        for (int i = 0; i < MAX_HAND_SIZE + 1; i++) {
            Card considered = this.knownHandCards[player.getId() - 1][i];
            if (considered != null && considered.equals(card)) {
                this.knownHandCards[player.getId() - 1][i] = null;
            }
        }
    }

}
