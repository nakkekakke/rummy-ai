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
        System.out.println("Enter thinking times for the AIs (in iterations)");
        System.out.println("Under 100 is very fast but bad play, over 10 000 is very slow but good play. For demoing I recommend 1000-5000");
        System.out.println("Enter AI #1's thinking time: ");
        int aiOneTime = Integer.parseInt(this.scanner.nextLine());
        
        System.out.println("Enter AI #2's thinking time: ");
        int aiTwoTime = Integer.parseInt(this.scanner.nextLine());
        
        ISMCTS aiOne = new ISMCTS(this.state, aiOneTime);
        ISMCTS aiTwo = new ISMCTS(this.state, aiTwoTime);
        
        int turnsPlayed = 0;
        
        while (!this.state.getAvailableMoves().isEmpty() && !this.state.roundOver()) {
            System.out.println(this.state);
            
            Move nextMove;
            
            if (this.state.getCurrentPlayer().getId() == 1) {
                aiOne.setRootState(this.state);
                nextMove = aiOne.run();
            } else {
                aiTwo.setRootState(this.state);
                nextMove = aiTwo.run();
            }
            
            this.state.doMove(nextMove);
            turnsPlayed++;
        }
        
        System.out.println("----------------------------");
        System.out.println("AI #" + this.state.getCurrentPlayer().getId() + " won this round!");
        System.out.println("They got " + this.state.calculateRoundPoints() + " points!");
        System.out.println("Turns played: " + turnsPlayed);
    }
}
