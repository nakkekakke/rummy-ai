package rummy.ai;

import java.util.Random;
import rummy.game.domain.Player;
import rummy.game.domain.State;
import rummy.game.domain.meld.Meld;
import rummy.game.domain.move.Move;

/**
 * Represents the Information Set Motne Carlo tree search (ISMCTS).
 */
public class ISMCTS {
    
    private State rootState; // The current state of the real game from which the algorithm tries to find the optimal move
    private State currentState;  // The current state of the simulated game
    private AIArrayList<Move> possibleMoves; // List of all possible moves from currentState
    private Node currentNode; // The node currently
    private final int limit; // Iteration limit for searching the best move
    private static final double EXPL = 0.7; // Exploration factor for UCT child selection, can be adjusted to make the AI play differently
    private final Random random;
    private int reshuffles; // The amount of times the deck has been exhausted without progress in the game (high reshuffles = potentially a never ending game)
    
    public ISMCTS(State rootState, int limit) {
        this.rootState = rootState;
        this.limit = limit;
        this.random = new Random();
        this.reshuffles = 0;
    }

    public State getRootState() {
        return this.rootState;
    }
    
    public void setRootState(State rootState) {
        this.rootState = rootState;
    }

    public int getLimit() {
        return this.limit;
    }
    
    /**
     * Runs the ISMCTS algorithm, searching for the optimal move from rootState.
     * @return the most promising move found
     */
    public Move run() {
        
        // Root node represents the current game situation
        Node rootNode = new Node(null, null, this.rootState.getCurrentPlayer());

        
        // Main loop of the tree search
        for (int i = 0; i < limit; i++) {
            this.currentNode = rootNode;
            
            // 1. Clone and determinize the state of the game (randomize information unknown to the AI)
            this.currentState = this.rootState.cloneAndRandomizeState();
            
            this.possibleMoves = this.currentState.getAvailableMoves();
            
            // 2. Select the most promising child node
            selectChildISMCTS();
            
            // 3. Expand the tree by creating a new child node for the selected node
            expandTreeISMCTS();
            
            // 4. Simulate by doing random moves from the expanded node until the game ends
            simulateISMCTS();
            
            // 5. Backpropagate the simulation result from the expanded node (in step 3) to every node along the way to the root
            backPropagateISMCTS();
        }
        
        //System.out.println(rootNode.treeToString(0)); // uncomment to visualize the tree, useful for debugging
        
        // Find the best move using backpropagated results
        Node best = rootNode.getChildren().get(0);
        for (int i = 0; i < rootNode.getChildren().size(); i++) {
            if (rootNode.getChildren().get(i).getVisits() > best.getVisits()) {
                best = rootNode.getChildren().get(i);
            }
        }
        return best.getMove();
    }
    
    private void selectChildISMCTS() {
        while ((!possibleMoves.isEmpty() && !this.currentState.roundOver()) && this.currentNode.getUntriedMoves(this.possibleMoves).isEmpty()) { // While every move option has been explored and the game hasn't ended
            this.currentNode = this.currentNode.selectChild(this.possibleMoves, EXPL); // Descend the tree
            this.currentState.doMove(this.currentNode.getMove(), true); // Update the state
            this.possibleMoves = this.currentState.getAvailableMoves(); // Possible moves change after a move so we have to redo this here
        }
        
    }
    
    private void expandTreeISMCTS() {
        AIArrayList<Move> untriedMoves = this.currentNode.getUntriedMoves(this.possibleMoves);
        if (!untriedMoves.isEmpty()) { // If the game didn't end yet
            Move randomMove = untriedMoves.get(this.random.nextInt(untriedMoves.size())); // Do a random move
            Player currentPlayer = this.currentState.getCurrentPlayer(); // Store current player in case the turn ends after the move
            if (this.possibleMoves.size() == 1 && randomMove.type().equals("pass")) { // If the only possible move is a "pass", do the move but skip creating a node out of it and simulating that because there is no decision to be made
                this.currentState.doMove(randomMove, false);
                this.possibleMoves = this.currentState.getAvailableMoves();
                selectChildISMCTS();
                expandTreeISMCTS();
            } else {
                this.currentNode = this.currentNode.addChild(randomMove, currentPlayer); // Add a child representing the new move and descend the tree
                this.currentState.doMove(randomMove, false);
            }
        }
    }
    
    private void simulateISMCTS() {
        this.possibleMoves = this.currentState.getAvailableMoves();
        AIArrayList<Meld> oldMelds = new AIArrayList<>();
        int oldPlayerId = this.currentState.getCurrentPlayer().getId();
        this.reshuffles = 0;

        while (!this.possibleMoves.isEmpty() && !this.currentState.roundOver()) {
            // Check if deck has been exhausted many times and melds have not changed (to detect never ending games / draws), this is buggy atm so the threshold is high)
            if (this.currentState.getDeck().length == 1 && oldPlayerId != this.currentState.getCurrentPlayer().getId()) {
                if (oldMelds.containsAll(this.currentState.getMelds())) {
                    this.reshuffles++;
                    if (this.reshuffles == 20) {
                        break;
                    }
                } else {
                    this.reshuffles = 0;
                }
            }
            oldMelds = new AIArrayList<>();
            for (Meld meld : this.currentState.getMelds()) {
                oldMelds.add(meld.copy());
            }
            oldPlayerId = this.currentState.getCurrentPlayer().getId();

            // Do the random move and update possible moves
            this.currentState.doMove(this.possibleMoves.get(this.random.nextInt(this.possibleMoves.size())), false);
            this.possibleMoves = this.currentState.getAvailableMoves();
        }
    }
    
    private void backPropagateISMCTS() {
        double result;
        if (this.reshuffles == 20) {
            result = 0.5;
        } else {
             result = this.currentState.getWinResult();
        }

        while (this.currentNode != null) {
            this.currentNode.update(this.currentState.getCurrentPlayer(), result);
            this.currentNode = currentNode.getParent();
        }
    }
}
