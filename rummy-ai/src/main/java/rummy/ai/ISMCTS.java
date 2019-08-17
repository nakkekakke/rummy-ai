package rummy.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import rummy.game.domain.Player;
import rummy.game.domain.State;
import rummy.game.domain.meld.Meld;
import rummy.game.domain.move.Move;

// The class representing the Information Set Monte Carlo tree search
public class ISMCTS {
    
    private State rootState; // The current state of the game from which the algorithm tries to find the optimal move
    private int limit; // Iteration limit for searching the best move
    private static final double EXPL = 0.7; // Exploration factor for UCT child selection, can be adjusted to make the AI play differently
    private Random random;
    
    public ISMCTS(State rootState, int limit) {
        this.rootState = rootState;
        this.limit = limit;
        this.random = new Random();
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
    
    public Move run() {
        
        // Root node represents the current game situation
        Node rootNode = new Node(null, null, this.rootState.getCurrentPlayer());
        
        long start = System.currentTimeMillis();
//        long end = start + (limit * 1000);
        
        int loopCounter = 0;
        
        // Main loop of the tree search
        while (loopCounter < limit) {
            Node currentNode = rootNode;
            
            // 1. Determinize the state of the game (randomize information unknown to the AI)
            State state = this.rootState.cloneAndRandomizeState();
            
            // 2. Select the most promising child node
            List<Move> possibleMoves = state.getAvailableMoves(); // Store possible moves to save method calls
            while ((!possibleMoves.isEmpty() && !state.roundOver()) && currentNode.getUntriedMoves(possibleMoves).isEmpty()) { // While every move option has been explored and the game hasn't ended
                currentNode = currentNode.selectChild(possibleMoves, EXPL); // Descend the tree
                state.doMove(currentNode.getMove()); // Update the state
                possibleMoves = state.getAvailableMoves(); // Possible moves change after a move so we have to redo this here
            }
            
            // 3. Expand the tree by creating a new child node for the selected node
            List<Move> untriedMoves = currentNode.getUntriedMoves(possibleMoves);
            if (!untriedMoves.isEmpty()) { // If the game didn't end yet
                Move randomMove = untriedMoves.get(this.random.nextInt(untriedMoves.size())); // Do a random move
                Player currentPlayer = state.getCurrentPlayer(); // Store current player in case the turn ends after the move
                state.doMove(randomMove);
                currentNode = currentNode.addChild(randomMove, currentPlayer); // Add a child representing the new move and descend the tree
            }
            
            
            possibleMoves = state.getAvailableMoves();
            List<Meld> oldMelds = new ArrayList<>();
            int oldPlayerId = state.getCurrentPlayer().getId();
            int reshuffles = 0;
            
            // 4. Simulate by doing random moves from the expanded node until the game ends
            while (!possibleMoves.isEmpty() && !state.roundOver()) {
                // Check if deck has been exhausted many times and melds have not changed (to detect never ending games / draws), this is buggy atm so the threshold is high)
                if (state.getDeck().length == 1 && oldPlayerId != state.getCurrentPlayer().getId()) {
                    if (oldMelds.containsAll(state.getMelds())) {
                        reshuffles++;
                        if (reshuffles == 20) {
                            break;
                        }
                    } else {
                        reshuffles = 0;
                    }
                }
                oldMelds = new ArrayList<>();
                oldMelds.addAll(state.getMelds());
                oldPlayerId = state.getCurrentPlayer().getId();
                
                // Do the random move and update possible moves
                state.doMove(possibleMoves.get(this.random.nextInt(possibleMoves.size())));
                possibleMoves = state.getAvailableMoves();
            }
            
            // 5. Backpropagate the simulation result from the expanded node (in step 3) to every node along the way to the root
            double result;
            if (reshuffles == 20) {
                result = 0.5;
            } else {
                 result = state.getWinResult();
            }
            
            while (currentNode != null) {
                currentNode.update(state.getCurrentPlayer(), result);
                currentNode = currentNode.getParent();
            }
            
            loopCounter++;
        }
        
        //System.out.println(rootNode.treeToString(0)); // uncomment to visualize the tree, useful for debugging
        
        // Find the best move using backpropagated results
        Node best = rootNode.getChildren().get(0);
        for (Node child : rootNode.getChildren()) {
            if (child.getVisits() > best.getVisits()) {
                best = child;
            }
        }
        
        //System.out.println("Simulated " + loopCounter + " games!");
        //long end = System.currentTimeMillis() - start;
        //System.out.println("This took " + end + " ms");
        return best.getMove();
    }
}
