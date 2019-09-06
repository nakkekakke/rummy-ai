package rummy.ai;

import rummy.game.domain.Player;
import rummy.game.domain.move.Move;

/**
 * Represents a node in the search tree.
 */
public class Node {
    
    private Node parent; // the parent of this node, null for root
    private AIArrayList<Node> children; // the child nodes of this node (one for every possible move from this node)
    
    private Move move; // move used to get to this node, null for root
    private Player player; // player who is doing the move to get to this node, the starting player for root
    private double totalScore; // sum of all simulation results from this node
    private int visits; // times this node has been selected
    private int considerations; // times this node has been considered for selection
    
    public Node(Node parent, Move move, Player player) {
        this.parent = parent;
        this.children = new AIArrayList<>();
        
        this.move = move;
        this.player = player;
        this.totalScore = 0.0;
        this.visits = 0;
        this.considerations = 1;
    }

    public Node getParent() {
        return this.parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public AIArrayList<Node> getChildren() {
        return this.children;
    }

    public void setChildren(AIArrayList<Node> children) {
        this.children = children;
    }

    public Move getMove() {
        return this.move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public double getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public int getVisits() {
        return this.visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getConsiderations() {
        return this.considerations;
    }

    public void setConsiderations(int considerations) {
        this.considerations = considerations;
    }
    
    /**
     * Returns all untried moves by filtering out all currently possible moves that have already been tried at least once.
     *
     * @param possibleMoves an AIArrayList of all possible moves
     * @return an AIArrayList of untried moves
     */
    public AIArrayList<Move> getUntriedMoves(AIArrayList<Move> possibleMoves) {
        AIArrayList<Move> triedMoves = new AIArrayList<>();
        for (int i = 0; i < this.children.size(); i++) {
            triedMoves.add(this.children.get(i).getMove());
        }
        
        AIArrayList<Move> untriedMoves = new AIArrayList<>();
        
        for (int i = 0; i < possibleMoves.size(); i++) {
            boolean tried = false;
            for (int j = 0; j < triedMoves.size(); j++) {
                if (possibleMoves.get(i).equals(triedMoves.get(j))) {
                    tried = true;
                    break;
                }   
            }
            
            if (!tried) {
                untriedMoves.add(possibleMoves.get(i));
            }
        }
        
        return untriedMoves;
    }
    
    /**
     * Returns the most promising child of this node (the child with the highest UCT score).
     * @param possibleMoves an AIArrayList of all possible moves
     * @param exploration the exploration factor for the UCT formula
     * @return the most promising child node
     */
    public Node selectChild(AIArrayList<Move> possibleMoves, double exploration) {
        Node selection = null;
        double selectionScore = -1.0;
        
        for (int i = 0; i < this.children.size(); i++) {
            Node child = this.children.get(i);
            if (possibleMoves.contains(child.move)) {
                double currentScore = calculateUCTScore(child, exploration);
                
                if (currentScore > selectionScore) {
                    selection = child;
                    selectionScore = currentScore; 
                }
                
                child.setConsiderations(child.getConsiderations() + 1);
            }
        }
        
        return selection;
    }
    
    // 
    /**
     * Adds a new child node for this node and returns it.
     * @param move the move that was used to get to the state of the game represented by the child
     * @param player the player who did the move
     * @return the newly created child node
     */
    public Node addChild(Move move, Player player) {
        Node newNode = new Node(this, move.copy(), player);
        this.children.add(newNode);
        return newNode;
    }
    
    /**
     * Updates visits and total score for this node. Used when backpropagating the simulation result.
     * @param winner the winner of the simulation
     * @param result the simulation result from the winners perspective
     */
    public void update(Player winner, double result) {
        this.visits++;
        if (winner.getId() == this.player.getId()) {
            this.totalScore += result;
        } else {
            this.totalScore += 1 - result;
        }
    }
    
    /**
     * Recursively prints the whole tree from this node down to the root. Used for debugging.
     * @param indent the size of the indentation when printing this node (0 when called the first time)
     * @return the string representing the tree
     */
    public String treeToString(int indent) {
        String s = indentString(indent) + this;
        for (int i = 0; i < this.children.size(); i++) {
            s += this.children.get(i).treeToString(indent + 1);
        }
        return s;
    }
    
    /**
     * Manages the printing indentation for different child levels. Used for debugging.
     * @param indent the size of the indentation when printing this node
     * @return the string representing the indentation
     */
    private String indentString(int indent) {
        String s = "\n";
        for (int i = 0; i < indent + 1; i++) {
            s += "| ";
        }
        return s;
    }
    
    @Override
    public String toString() {
        if (this.move == null) {
            return this.player.getId() + " ROOT, SCORE: " + this.totalScore + ", VISITS: " + this.visits + ", CONSIDERATIONS: " + this.considerations;
        } else if (this.parent.move == null) {
            return this.player.getId() + " MOVE: " + this.move + ", SCORE: " + this.totalScore + ", VISITS: " + this.visits + ", CONSIDERATIONS: " + this.considerations + ", PARENT: ROOT";
        }
        return this.player.getId() + " MOVE: " + this.move + ", SCORE: " + this.totalScore + ", VISITS: " + this.visits + ", CONSIDERATIONS: " + this.considerations + ", PARENT TYPE: " + this.parent.move.type();
    }
    
    /**
     * Basic UCT formula used for calculating the next node selection.
     */
    private double calculateUCTScore(Node node, double exploration) {
        return ( node.getTotalScore() / node.getVisits() ) + ( exploration * Math.sqrt(Math.log(node.getConsiderations()) / node.getVisits()) );
    }
}
