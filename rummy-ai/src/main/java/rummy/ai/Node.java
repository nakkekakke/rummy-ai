package rummy.ai;

import java.util.ArrayList;
import java.util.List;
import rummy.game.domain.Player;
import rummy.game.domain.move.Move;

public class Node {
    
    private Node parent; // the parent of this node, null for root
    private List<Node> children; // the child nodes of this node (one for every possible move from this node)
    
    private Move move; // move used to get to this node, null for root
    private Player player; // player who is doing the move to get to this node, the starting player for root
    private double totalScore; // sum of all simulation results from this node
    private int visits; // times this node has been selected
    private int considerations; // times this node has been considered for selection
    
    public Node(Node parent, Move move, Player player) {
        this.parent = parent;
        this.children = new ArrayList<>();
        
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

    public List<Node> getChildren() {
        return this.children;
    }

    public void setChildren(List<Node> children) {
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
    
    // Filter out all currently possible moves that have already been tried at least once
    public List<Move> getUntriedMoves(List<Move> possibleMoves) {
        List<Move> triedMoves = new ArrayList<>();
        for (Node child : this.children) {
            triedMoves.add(child.move);
        }
        
        List<Move> untriedMoves = new ArrayList<>();
        
        for (Move possibleMove : possibleMoves) {
            boolean tried = false;
            for (Move triedMove : triedMoves) {
                if (possibleMove.equals(triedMove)) {
                    tried = true;
                    break;
                }   
            }
            
            if (!tried) {
                untriedMoves.add(possibleMove);
            }
        }
        
        return untriedMoves;
    }
    
    // Return the most promising child of this node (the highest UCT score)
    public Node selectChild(List<Move> possibleMoves, double exploration) {
        Node selection = null;
        double selectionScore = -1.0;
        
        for (Node child : this.children) {
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
    
    // Add a new child node for this node and return it
    public Node addChild(Move move, Player player) {
        Node newNode = new Node(this, move.copy(), player);
        this.children.add(newNode);
        return newNode;
    }
    
    // Update visits and total score for this node
    public void update(Player winner, double result) {
        this.visits++;
        if (winner.getId() == this.player.getId()) {
            this.totalScore += result;
        } else {
            this.totalScore += 1 - result;
        }
    }
    
    // Print the tree down from this node
    public String treeToString(int indent) {
        String s = indentString(indent) + this;
        for (Node child : this.children) {
            s += child.treeToString(indent + 1);
        }
        return s;
    }
    
    // Printing indentation for different child levels
    public String indentString(int indent) {
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
    
    // Basic UCT formula used for calculation
    private double calculateUCTScore(Node node, double exploration) {
        return ( node.getTotalScore() / node.getVisits() ) + ( exploration * Math.sqrt(Math.log(node.getConsiderations()) / node.getVisits()) );
    }
}
