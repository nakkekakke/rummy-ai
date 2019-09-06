package rummy.game.domain;

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
import rummy.game.domain.move.PassMove;
import rummy.ai.AIArrayList;

// 

/** 
 * The class representing the state of the game. It is directly changed by doing moves to advance the game.
 */
public class State {
    
    private static final int DECK_SIZE = 52; // Rummy uses a standard 52 card deck
    private static final int MAX_HAND_SIZE = 10; // Players can have up to 10 cards in their hands after a turn ends (11 after draw and before discarding)
    private static final int PLAYER_COUNT = 2; // This version is a 2 player game

    private Player currentPlayer; // The player whose turn it is now
    private Player waitingPlayer; // The player whose turn it isn't
    private Card[] deck;
    private Stack<Card> discardPile;
    private List<Meld> melds; // Melds played this round
    private String phase; // Current phase of the game
    //private static final String[] phases = {"draw", "meld", "layoff", "discard", "end"};
    private Meld currentMeld; // Meld used this turn
    private Card[][] knownHandCards; // Players' hand cards that are known to both players (cards drawn from the discard pile)
    private Card discardDraw; // The card that was drawn from the discard pile this turn (this card cannot be discarded at the end of this turn)
    

    /**
     * Used to start a fresh new game of Rummy.
     * @param currentPlayerId the id of the player who will start the game.
     */
    public State(int currentPlayerId) {
        this.currentPlayer = new Player(currentPlayerId);
        this.waitingPlayer = new Player((currentPlayerId % PLAYER_COUNT) + 1);
        this.discardPile = new Stack<>();
        this.melds = new ArrayList<>();
        this.phase = "draw";
        this.currentMeld = null;
        this.knownHandCards = new Card[PLAYER_COUNT][MAX_HAND_SIZE + 1];
        this.discardDraw = null;
        this.deal();
    }

    /**
     * Used to start a new round in the game. This means that the player points are saved.
     * @param currentPlayer the id of the player who will start the new round.
     * @param waitingPlayer the id of the other player.
     */
    public State(Player currentPlayer, Player waitingPlayer) {
        this.currentPlayer = currentPlayer;
        this.waitingPlayer = waitingPlayer;
        this.discardPile = new Stack<>();
        this.melds = new ArrayList<>();
        this.phase = "draw";
        this.currentMeld = null;
        this.knownHandCards = new Card[PLAYER_COUNT][MAX_HAND_SIZE + 1];
        this.discardDraw = null;
        this.deal();
    }

    /**
     * Constructor used to clone a state. All of the parameters will be added into the clone.
     * @param currentPlayer
     * @param waitingPlayer
     * @param deck
     * @param discardPile
     * @param melds
     * @param phase
     * @param knownHandCards
     * @param currentMeld
     * @param discardDraw
     */
    public State(Player currentPlayer, Player waitingPlayer, Card[] deck, Stack<Card> discardPile, List<Meld> melds, String phase, Card[][] knownHandCards, Meld currentMeld, Card discardDraw) {
        this.currentPlayer = currentPlayer;
        this.waitingPlayer = waitingPlayer;
        this.deck = deck;
        this.discardPile = discardPile;
        this.melds = melds;
        this.phase = phase;
        this.knownHandCards = knownHandCards;
        this.currentMeld = currentMeld;
        this.discardDraw = discardDraw;
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
    
    /**
     * Gets the top card of the discard pile without removing it from the pile.
     * @return the top card of the discard pile
     */
    public Card getDiscardPileTop() {
        return this.discardPile.peek();
    }
    
    /**
     *
     * @return the current amount of cards in the deck
     */
    public int getDeckSize() {
        return this.deck.length;
    }
    
    public int getOpponentHandSize() {
        return this.waitingPlayer.getHand().size();
    }

    public Card[][] getKnownHandCards() {
        return this.knownHandCards;
    }
    
    // Done at the start of every round
    private void deal() {
        this.deck = shuffleDeck(createDeck());
        initializeHands();
        initialDraw();
    }
    

    /**
     * Creates a fresh 52-card deck (not shuffled)
     * @return a full deck with cards in order
     */
    public final Card[] createDeck() {
        Card[] newDeck = new Card[DECK_SIZE];
        
        int i = 0;
        for (Suit suit : Suit.values()) {
            for (int rank = 1; rank <= 13; rank++) {
                newDeck[i] = (new Card(suit, rank));
                i++;
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
    
    // Deals cards to both players' hands and shuffles them
    private void initializeHands() {
        this.currentPlayer.setHand(new ArrayList<>());
        this.waitingPlayer.setHand(new ArrayList<>());
        for (int i = 0; i < MAX_HAND_SIZE * 2; i += 2) {
            this.currentPlayer.addToHand(this.deck[i]);
            this.waitingPlayer.addToHand(this.deck[i + 1]);
        }
        
        this.currentPlayer.organizeHand();
        this.waitingPlayer.organizeHand();
        
        Card[] newDeck = new Card[this.deck.length - 20];
        for (int i = 0; i < newDeck.length; i++) {
            newDeck[i] = this.deck[i + 20];
        }
        
        this.deck = newDeck;
    }
    

    /**
     * Done during every deal, a card from the deck is added to the empty discard pile.
     */
    public void initialDraw() {
        Card draw = this.deck[0];
        Card[] newDeck = shortenDeckByN(this.deck, 1);
        
        this.deck = newDeck;
        this.discardPile.push(draw);
    }
    

    /**
     * Returns a card drawn from the deck and removes that card from the deck.
     * @return the top card of the deck
     */
    public Card drawFromDeck() {
        Card draw = this.deck[0];
        Card[] newDeck = shortenDeckByN(this.deck, 1);
        this.deck = newDeck;
        
        if (newDeck.length == 0) {
            this.deck = this.refillDeckFromDiscardPile();
        }
        
        this.currentPlayer.addToHand(draw);
        this.currentPlayer.organizeHand();
        this.phase = "meld";
        return draw;
    }
    
    // Removes n cards from the top of the deck
    private Card[] shortenDeckByN(Card[] deck, int n) {
        Card[] newDeck = new Card[deck.length - n];
        for (int i = 0; i < newDeck.length; i++) {
            newDeck[i] = deck[i + n];
        }
        return newDeck;
    }
    
    // After the deck runs out, a new deck is formed out of the discard pile
    private Card[] refillDeckFromDiscardPile() {
        Card discardTop = this.discardPile.pop();
        Card[] newDeck = new Card[this.discardPile.size()];
        
        for (int i = 0; i < newDeck.length; i++) {
            newDeck[i] = this.discardPile.pop();
        }
        
        this.discardPile.push(discardTop);
        return shuffleDeck(newDeck);
    }
    
    /**
     * Sorts the current hand.
     */
    public void organizeCurrentHand() {
        this.currentPlayer.organizeHand();
    }
    
    /**
     * Draws a card from the discard pile and adds it to the current player's hand. Also advances the game phase to "meld".
     * @return the top card of the discard pile
     */
    public Card drawFromDiscardPile() {
        Card draw = this.discardPile.pop();
        this.currentPlayer.addToHand(draw);
        this.currentPlayer.organizeHand();
        this.phase = "meld";
        addToKnownHandCards(draw, this.currentPlayer);
        this.discardDraw = draw;
        return draw;
    }
    

    /**
     * Finds all possible melds the current player can do from their hand. Assumes that hand cards are in order!
     * @return a list of all possible melds for the current player
     */
    public List<Meld> findPossibleMelds() {
        List<Meld> possibleMelds = new ArrayList<>();
        List<Card> hand = this.currentPlayer.getHand();
        
        int[][] ranks = new int[13][5]; // The first column is rank count (index is rank number), 
                                        // The other 4 columns are for card indices in hand (indices are +1 so 0 means that card isn't in hand) for each possible rank card
        
        // (RUN MELDS) MAIN LOOP, represents the lower index
        for (int i = 0; i < hand.size(); i++) {
            
            // Count ranks while searching for run melds for extra efficiency
            ranks[hand.get(i).getRank() - 1][0]++; 
            for (int x = 1; x < 5; x++) {
                if (ranks[hand.get(i).getRank() - 1][x] == 0) {
                    ranks[hand.get(i).getRank() - 1][x] = i + 1;
                    break;
                } 
            }
            
            // (RUN MELDS) SECONDARY LOOP, represents the higher index. Finds all possible run melds of any length (>= 3) between the two indices
            for (int j = i + 1; j < hand.size(); j++) {
                
                if ( (hand.get(i).getSuit() == hand.get(j).getSuit()) && (hand.get(i).getRank() == hand.get(j).getRank() - (j - i)) ) {
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
        
        // All run melds found, now find all set melds using rank data gathered earlier
        
        // (SET MELDS) MAIN LOOP
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
                
                // Loop through all 5 indices in one ranks[][] row (array length 5), skipping the first value (count). Also one extra loop to create one 4-long meld.
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
    

    /**
     * Creates a meld, removes the melded cards from the hand and starts the layoff phase. The parameter meld will be cloned and the clone will be used.
     * @param meld the meld to be added to the game melds
     * @return the meld used as parameter
     */
    public Meld meld(Meld meld) {
        for (int i = 0; i < meld.getCards().size(); i++) {
            this.currentPlayer.discard(meld.getCards().get(i));
            removeFromKnownHandCards(meld.getCards().get(i), this.currentPlayer);
        }
        this.melds.add(meld.copy());
        this.currentMeld = meld;
        this.phase = "layoff";
        return meld;
    }
    

    /**
     * Finds all possible layoffs for the current player.
     * @return a list of possible layoffs
     */
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

    /**
     * Creates a layoff and removes the laid-off card from hand. The parameter layoff will be cloned before using.
     * @param layoff the layoff to be done
     * @param aiSelection true if this method is being used by the AI's selection phase, otherwise false
     */
    public void layoff(Layoff layoff, boolean aiSelection) {
        this.currentPlayer.discard(layoff.getCard());
        removeFromKnownHandCards(layoff.getCard(), this.currentPlayer);
        Meld meld = layoff.getMeld();
        
        if (!aiSelection) {
            for (int i = 0; i < this.melds.size(); i++) {
                if (this.melds.get(i).getCards().equals(meld.getCards())) {
                    meld.layoff(layoff.getCard());
                    this.melds.set(i, meld);
                }
            }
        } else {
            layoff.getMeld().copy().layoff(layoff.getCard());
        }
    }
    

    /**
     * Starts the discard phase. This is called after all wanted layoffs have been done.
     */
    public void layoffDone() {
        this.phase = "discard";
    }
    

    /**
     * Discards hand card by index.
     * @param number the index of the card in hand
     * @return the discarded card
     */
    public Card discardCardNumber(int number) {
        Card card = this.currentPlayer.getHand().get(number);
        this.discard(card);
        return card;
    }
    

    /**
     * Discards a hand card and starts the end phase.
     * @param card the card to be discarded
     */
    public void discard(Card card) {
        this.discardPile.push(card);
        this.currentPlayer.discard(card);
        removeFromKnownHandCards(card, this.currentPlayer);
        this.currentMeld = null;
        this.phase = "end";
    }
    
    /**
     * Swaps the current and the waiting player, sets the game phase to draw.
     */
    public void endTurn() {
        Player current = this.currentPlayer;
        this.currentPlayer = this.waitingPlayer;
        this.waitingPlayer = current;
        this.phase = "draw";
        this.discardDraw = null;
    }
    

    /**
     * Calculates the points for the winner of this round. Must be called during the winning player's turn.
     * @return the sum of the points awarded from this round
     */
    public int calculateRoundPoints() {
        int sum = 0;
        for (int i = 0; i < this.waitingPlayer.getHand().size(); i++) {
            int rank = this.waitingPlayer.getHand().get(i).getRank();
            if (rank >= 10) {
                sum += 10;
            } else {
                sum += rank;
            }
        }
        return sum;
    }
    
    /**
     * Updates the points of the winner of this round automatically.
     */
    public void updateWinnerPoints() {
        this.currentPlayer.setPoints(this.currentPlayer.getPoints() + calculateRoundPoints());
    }
    
    /**
     * Starts a new round. Creates a new game state that will be used for the next round.
     * @return the new game state
     */
    public State startNewRound() {
        return new State(this.waitingPlayer, this.currentPlayer); // loser starts the next round
    }
    
    /**
     *
     * @return true if a card has been discarded and it's time to end the turn, otherwise false
     */
    public boolean turnOver() {
        return this.phase.equals("end");
    }
        
    /**
     *
     * @return true if the current player's hand is empty, otherwise false
     */
    public boolean roundOver() {
        return currentPlayer.getHand().isEmpty();
    }
    
    /**
     *
     * @return true if the current player has won the whole game, otherwise false
     */
    public boolean gameOver() {
        return this.currentPlayer.getPoints() >= 100;
    }
    
    @Override
    public String toString() {
        return "Current player: " + this.currentPlayer.getId() + ", " + this.currentPlayer.getHand() + ", waiting player: " + this.waitingPlayer.getId() + ", " + this.waitingPlayer.getHand() + ", melds: " + this.melds;
    }
    
    
    
    //
    // "THE AI METHODS"
    //
    

    /**
     * Returns all possible moves from this game phase.
     * @return a list of all possible moves
     */
    public AIArrayList<Move> getAvailableMoves() {
        AIArrayList<Move> moves = new AIArrayList<>();
        if (roundOver()) {
            return moves;
        }
        switch (this.phase) {
            case "draw":
                moves.add(new DrawMove(this.currentPlayer, true));
                moves.add(new DrawMove(this.currentPlayer, false));
                break;
            case "meld":
                for (Meld meld : findPossibleMelds()) {
                    moves.add(new MeldMove(this.currentPlayer, meld));
                }
                moves.add(new PassMove(this.currentPlayer, "meld"));
                break;
            case "layoff":
                for (Layoff layoff : findPossibleLayoffs()) {
                    moves.add(new LayoffMove(this.currentPlayer, layoff));
                }
                moves.add(new PassMove(this.currentPlayer, "layoff"));
                break;
            case "discard":
                for (Card card : this.currentPlayer.getHand()) {
                    if (this.discardDraw == null || !card.equals(this.discardDraw)) {
                        moves.add(new DiscardMove(this.currentPlayer, card));
                    }
                }
                break;
            case "end":
                moves.add(new PassMove(this.currentPlayer, "end"));
            default:
                break;
        }
        
        return moves;
    }

    /**
     * Does a move and changes this state directly.
     * @param move the move to be done
     * @param aiSelection true if this method is being used by the AI's selection phase, otherwise false
     */
    public void doMove(Move move, boolean aiSelection) {
        switch (move.type()) {
            case "draw":
                doDrawMove((DrawMove) move);
                break;
            case "meld":
                doMeldMove((MeldMove) move);
                break;
            case "layoff":
                doLayoffMove((LayoffMove) move, aiSelection);
                break;
            case "discard":
                doDiscardMove((DiscardMove) move);
                break;
            case "pass":
                doPassMove();
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
    
    private void doLayoffMove(LayoffMove move, boolean aiSelection) {
        layoff(move.getLayoff(), aiSelection);
    }
    
    private void doDiscardMove(DiscardMove move) {
        discard(move.getCard());
    }
    
    /**
     * Only advances the game state. Ends the turn if game state was "end".
     */
    private void doPassMove() {
        if (this.phase.equals("meld")) {
            this.phase = "layoff";
        } else if (this.phase.equals("layoff")) {
            this.phase = "discard";
        } else if (this.phase.equals("end")) {
            endTurn();
        }
    }
    

    /**
     * Creates a "deep" clone of this state (deep enough for the AI to consider them the same).
     * @return the cloned state
     */
    @SuppressWarnings("unchecked")
    public State cloneState() {
        List<Meld> cloneMelds = new ArrayList<>();
        
        for (int i = 0; i < this.melds.size(); i++) {
            Meld meld = this.melds.get(i);
            Meld cloneMeld;
            
            LinkedList<Card> cloneMeldCards = new LinkedList<>();
            cloneMeldCards.addAll(meld.getCards());
            
            if (meld.type().equals("run")) {
                cloneMeld = new RunMeld(new Player(meld.getPlayer()), cloneMeldCards);
            } else {
                cloneMeld = new SetMeld(new Player(meld.getPlayer()), cloneMeldCards);
            }
            
            cloneMelds.add(cloneMeld);
        }
        
        Stack<Card> discardPileClone = new Stack<>();
        discardPileClone.addAll(discardPile);
        
        if (this.currentMeld != null) {
            return new State(
                new Player(this.currentPlayer), 
                new Player(this.waitingPlayer), 
                this.deck.clone(), 
                discardPileClone, 
                cloneMelds, 
                this.phase, 
                this.knownHandCards.clone(), 
                this.currentMeld.copy(),
                this.discardDraw
            );
        }
        
        return new State(
                new Player(this.currentPlayer), 
                new Player(this.waitingPlayer), 
                this.deck.clone(), 
                discardPileClone, 
                cloneMelds, 
                this.phase, 
                this.knownHandCards.clone(), 
                null,
                this.discardDraw
        );
    }
    

    /**
     * Clones the current state, then randomizes all hidden information of the state from the AI's perspective. Applies this randomization to the cloned state.
     * @return the newly cloned and randomized state
     */
    public State cloneAndRandomizeState() {
        State clone = cloneState();
        
        Card[] knownCards = new Card[DECK_SIZE];
        int knownIndex = 0;
        
        // Add own hand cards to known cards
        for (Card card : clone.getCurrentPlayer().getHand()) {
            knownCards[knownIndex] = card;
            knownIndex++;
        }
        
        // Add discard pile to known cards
        for (Card card : clone.getDiscardPile()) {
            knownCards[knownIndex] = card;
            knownIndex++;
        }
         
        // Add all melds to known cards
        for (Meld meld : clone.getMelds()) {
            for (Card card : meld.getCards()) {
                knownCards[knownIndex] = card;
                knownIndex++;
            }
        }

        // Add already known opponent's hand cards to known cards
        for (int j = 0; j < MAX_HAND_SIZE + 1; j++) {
            if (clone.getKnownHandCards()[clone.getWaitingPlayer().getId() - 1][j] != null) {
                knownCards[knownIndex] = clone.getKnownHandCards()[clone.getWaitingPlayer().getId() - 1][j];
                knownIndex++;
            }
        }
        
        int unknownIndex = 0;
        Card[] unknownCards = new Card[DECK_SIZE - knownIndex];
        Card[] fullDeck = createDeck();
        
        // Add every card except the known cards to unknown cards
        for (int i = 0; i < DECK_SIZE; i++) {
            boolean known = false;
            
            for (int j = 0; j < knownIndex; j++) {
                if (fullDeck[i].equals(knownCards[j])) {
                    known = true;
                    break;
                }
            }
            
            if (!known) {
                unknownCards[unknownIndex] = fullDeck[i];
                unknownIndex++;
            }
        }
        
        unknownCards = shuffleDeck(unknownCards);

        Card[] opponentKnownCards = clone.getKnownHandCards()[clone.getWaitingPlayer().getId() - 1];
        ArrayList<Card> opponentHandGuess = new ArrayList<>();
        
        // Add cards that are known to be in opponent's hand to "opponent's hand guess"
        for (int i = 0; i < opponentKnownCards.length; i++) {
            if (opponentKnownCards[i] != null) {
                opponentHandGuess.add(opponentKnownCards[i]);
            }
        }
        
        // Add random cards to "opponent's hand guess" until the size is same as the actual hand
        int i;
        int handSize = opponentHandGuess.size();
        for (i = 0; i < clone.getOpponentHandSize() - handSize; i++) {
            opponentHandGuess.add(unknownCards[i]);
        }
        
        unknownCards = shortenDeckByN(unknownCards, i);
        
        // Set randomized hidden data to the cloned state
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

    /**
     * Gets the round result from the winner's perspective. The result is linearly scaled between 0.5 and 1.0 so that 0.5 means a draw and 1.0 means a win by 100 points. This value is used by the AI.
     * @return the win result between 0.505 (inclusive) and 1.0 (inclusive)
     */
    public double getWinResult() {
        double result;
        int points = calculateRoundPoints();
        
        if (points >= 100) {
            result = 1;
        } else {
            result = 0.5 + (points / 200.0);
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
