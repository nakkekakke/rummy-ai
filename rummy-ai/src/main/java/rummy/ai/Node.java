package rummy.ai;

import java.util.ArrayList;
import java.util.List;
import rummy.game.domain.Player;
import rummy.game.domain.move.Move;

public class Node {
    
    private Node parent;
    private List<Node> children;
    
    private Move move;
    private Player player;
    private int totalScore;
    private int visits;
    private int considerations; // times this node has been considered but not selected
    
    public Node(Node parent, Move move, Player player) {
        this.parent = parent;
        this.children = new ArrayList<>();
        
        this.move = move;
        this.player = player;
        this.totalScore = 0;
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

    public int getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore(int totalScore) {
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
    
    public Node addChild(Move move, Player player) {
        Node newNode = new Node(this, move, player);
        this.children.add(newNode);
        return newNode;
    }
    
    public void update(Player winner, double result) {
        this.visits++;
        if (winner.getId() == this.player.getId()) {
            this.totalScore += result;
        } else {
            this.totalScore += 1 - result;
        }
    }
    
    public String treeToString(int indent) {
        String s = indentString(indent) + this;
        for (Node child : this.children) {
            s += child.treeToString(indent + 1);
        }
        return s;
    }
    
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
            return "ROOT, SCORE: " + this.totalScore + ", VISITS: " + this.visits + ", CONSIDERATIONS: " + this.considerations;
        } else if (this.parent.move == null) {
            return "MOVE: " + this.move.type() + ", SCORE: " + this.totalScore + ", VISITS: " + this.visits + ", CONSIDERATIONS: " + this.considerations + ", PARENT: ROOT";
        }
        return "MOVE: " + this.move.type() + ", SCORE: " + this.totalScore + ", VISITS: " + this.visits + ", CONSIDERATIONS: " + this.considerations + ", PARENT TYPE: " + this.parent.move.type();
    }
    
    private double calculateUCTScore(Node node, double exploration) {
        return ( node.getTotalScore() / node.getVisits() ) + ( exploration * Math.sqrt(Math.log(node.getConsiderations()) / node.getVisits()) );
    }
}
