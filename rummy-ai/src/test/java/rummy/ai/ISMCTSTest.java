package rummy.ai;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import rummy.game.domain.State;
import rummy.game.domain.move.Move;

public class ISMCTSTest {
    
    private ISMCTS ai;

    @Before
    public void setUp() {
        this.ai = new ISMCTS(new State(1), 100);
    }
    
    @Test
    public void aiWorksCorrectly() {
        Move bestMove = this.ai.run();
        assertEquals("draw", bestMove.type());
        
        State state = this.ai.getRootState();
        state.doMove(bestMove, false);
        this.ai.setRootState(state);
        
        bestMove = this.ai.run();
        
        state = this.ai.getRootState();
        state.doMove(bestMove, false);
        this.ai.setRootState(state);
        
        this.ai.run();
    }

}