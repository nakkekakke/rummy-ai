package rummy.ai;

import java.util.List;
import java.util.Random;
import rummy.game.domain.Player;
import rummy.game.domain.State;
import rummy.game.domain.move.Move;


public class ISMCTS {
    
    private State rootState;
    private int limit; // time limit for searching the best move (seconds)
    private static final double EXPL = 0.7; // exploration factor for UCT child selection
    private Random random;
    
    public ISMCTS(State rootState, int limit) {
        this.rootState = rootState;
        this.limit = limit;
        this.random = new Random();
    }

    public State getRootState() {
        return this.rootState;
    }

    public int getLimit() {
        return this.limit;
    }
    
    public Move run() {
        
        Node rootNode = new Node(null, null, this.rootState.getCurrentPlayer());
        
        long start = System.currentTimeMillis();
        long end = start + (limit * 1000);
        
        while (start < end) {
            Node currentNode = rootNode;
            
            // 1. Determinize the state
            State state = this.rootState.cloneAndRandomizeState();
            
            // 2. Select the most promising child node
            List<Move> possibleMoves = state.getAvailableMoves(); // store possible moves to save method calls
            while (!possibleMoves.isEmpty() && currentNode.getUntriedMoves(possibleMoves).isEmpty()) { // while every move option has been explored and the game hasn't ended
                currentNode = currentNode.selectChild(possibleMoves, EXPL); // descend the tree
                state.doMove(currentNode.getMove()); // update the state
                possibleMoves = state.getAvailableMoves(); // possible moves change after a move so we have to redo this here
            }
            
            // 3. Expand the tree by creating a new child node for the selected node
            List<Move> untriedMoves = currentNode.getUntriedMoves(possibleMoves);
            if (!untriedMoves.isEmpty()) { // if the game didn't end yet
                Move randomMove = untriedMoves.get(this.random.nextInt(untriedMoves.size())); // choose a random move
                Player currentPlayer = state.getCurrentPlayer(); // store current player in case the turn ends after the move
                state.doMove(randomMove);
                currentNode = currentNode.addChild(randomMove, currentPlayer); // add a child and descend the tree
            }
            
            // 4. Simulate by doing random moves until the game ends
            possibleMoves = state.getAvailableMoves();
            while (!possibleMoves.isEmpty()) {
                state.doMove(possibleMoves.get(this.random.nextInt(possibleMoves.size())));
                possibleMoves = state.getAvailableMoves();
            }
            
            // 5. Backpropagate the result from the expanded node back to the root
            double result = state.getWinResult();
            while (currentNode != null) {
                currentNode.update(state.getCurrentPlayer(), result);
                currentNode = currentNode.getParent();
            }
        }
        
        Node best = rootNode.getChildren().get(0);
        
        for (Node child : rootNode.getChildren()) {
            if (child.getVisits() > best.getVisits()) {
                best = child;
            }
        }
        
        return best.getMove();
    }

}
