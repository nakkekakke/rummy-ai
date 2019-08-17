package rummy.ai;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rummy.game.domain.Player;
import rummy.game.domain.move.Move;
import rummy.testutil.TestUtil;

public class NodeTest {
    
    private Node node;

    @Before
    public void setUp() {
        this.node = new Node(null, null, new Player(1));
        
    }

    @Test
    public void getUntriedMovesReturnsAllIfNoTriedMoves() {
        List<Move> allMoves = TestUtil.getAvailableMoves();
        
        List<Move> untriedMoves = this.node.getUntriedMoves(allMoves);
        
        assertEquals(allMoves, untriedMoves);
    }
    
    @Test
    public void getUntriedMovesFiltersOutTriedMoves() {
        List<Move> allMoves = TestUtil.getAvailableMoves();
        
        assertEquals(2, allMoves.size());
        Move nowTried = allMoves.get(0);
        
        Node child = new Node(this.node, nowTried, new Player(1));
        
        this.node.getChildren().add(child);
        
        List<Move> untriedMoves = this.node.getUntriedMoves(allMoves);
        
        assertEquals(allMoves.size() - 1, untriedMoves.size());
        assertFalse(untriedMoves.contains(nowTried));
    }
    
    @Test
    public void selectChildChoosesCorrectChild() {
        List<Move> possibleMoves = TestUtil.getAvailableMoves("meld");
        TestUtil.createSearchTree(this.node);
        Node drawNode = this.node.getChildren().get(0);
        Node child = drawNode.selectChild(possibleMoves, 0.7);
        
        assertEquals(drawNode.getChildren().get(0), child);
    }

}