package rummy.ai;

import java.util.ArrayList;
import java.util.List;
import rummy.game.domain.Player;
import rummy.game.domain.move.Move;

public class Node {
    
    private Node parent;
    private List<Node> children;
    
    private Move previousMove;
    private Player player;
    private int totalScore;
    private int visits;
    private int parentVisits;
    
    public Node(Node parent, Move previousMove, Player player) {
        this.parent = parent;
        this.children = new ArrayList<>();
        
        this.previousMove = previousMove;
        this.player = player;
        this.totalScore = 0;
        this.visits = 0;
        this.parentVisits = 1;
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

    public Move getPreviousMove() {
        return this.previousMove;
    }

    public void setPreviousMove(Move previousMove) {
        this.previousMove = previousMove;
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

    public int getParentVisits() {
        return this.parentVisits;
    }

    public void setParentVisits(int parentVisits) {
        this.parentVisits = parentVisits;
    }
    
    
    public List<Move> getUntriedMoves(List<Move> possibleMoves) {
        List<Move> triedMoves = new ArrayList<>();
        for (Node child : this.children) {
            triedMoves.add(child.previousMove);
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
}
