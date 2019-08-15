package rummy.game.ui;

import java.util.Scanner;
import rummy.ai.ISMCTS;
import rummy.game.domain.State;
import rummy.game.domain.move.Move;


public class AIvsAI_UI {
    
    private State state;
    private Scanner scanner;
    
    public AIvsAI_UI(State state) {
        this.state = state;
        this.scanner = new Scanner(System.in);
    }
    
    public void play() {
        System.out.println("AI vs AI game starting!");
        System.out.println("Enter AI #1's thinking time in seconds: ");
        int aiOneTime = Integer.parseInt(this.scanner.nextLine());
        
        System.out.println("Enter AI #2's thinking time in seconds: ");
        int aiTwoTime = Integer.parseInt(this.scanner.nextLine());
        
        ISMCTS aiOne = new ISMCTS(this.state, aiOneTime);
        ISMCTS aiTwo = new ISMCTS(this.state, aiTwoTime);
        
        while (!this.state.getAvailableMoves().isEmpty() && !this.state.roundOver()) {
            System.out.println("It's player " + this.state.getCurrentPlayer().getId() + "'s turn!");
            System.out.println("His hand is " + this.state.getCurrentPlayer().getHand());
            
            Move nextMove;
            
            if (this.state.getCurrentPlayer().getId() == 1) {
                aiOne.setRootState(this.state);
                nextMove = aiOne.run();
            } else {
                aiTwo.setRootState(this.state);
                nextMove = aiTwo.run();
            }
            
            System.out.println("Next move by player " + this.state.getCurrentPlayer().getId() + " is: " + nextMove.toString());
            this.state.doMove(nextMove);
            if (this.state.getCurrentPlayer().getId() == 2) {
                break;
            }
        }
        
        System.out.println("AI #" + this.state.getCurrentPlayer().getId() + " won this round!");
        System.out.println("They got " + this.state.calculateRoundPoints() + " points!");
        
        System.out.println(this.state.getCurrentPlayer().getHand());
    }
}
