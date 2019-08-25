package rummy.game.ui;

import java.util.Scanner;
import rummy.ai.ISMCTS;
import rummy.game.domain.State;
import rummy.game.domain.move.Move;


public class AIvsAI_UI {
    
    private State state;
    private final Scanner scanner;
    
    public AIvsAI_UI(int startingPlayer, Scanner scanner) {
        this.state = new State(startingPlayer);
        this.scanner = scanner;
    }
    
    public void play() {
        System.out.println("AI vs AI game starting!");
        System.out.println("Enter thinking times for the AIs (in iterations)");
        System.out.println("Under 100 is very fast but bad play, over 10 000 is very slow but good play. For demoing I recommend 1000-5000");
        System.out.println("Enter AI #1's thinking time: ");
        int aiOneTime = Integer.parseInt(this.scanner.nextLine());
        
        System.out.println("Enter AI #2's thinking time: ");
        int aiTwoTime = Integer.parseInt(this.scanner.nextLine());
        
        int roundCount = 0;
        while (true) {
            System.out.println("Starting a new round!");


            int turnsPlayed = 0;

            while (!this.state.getAvailableMoves().isEmpty()) {
                System.out.println(this.state);
                ISMCTS aiOne = new ISMCTS(this.state, aiOneTime);
                ISMCTS aiTwo = new ISMCTS(this.state, aiTwoTime);
                Move nextMove;

                if (this.state.getCurrentPlayer().getId() == 1) {
                    aiOne.setRootState(this.state);
                    nextMove = aiOne.run();
                } else {
                    aiTwo.setRootState(this.state);
                    nextMove = aiTwo.run();
                }
                
                System.out.println("Next move " + nextMove);
                this.state.doMove(nextMove, false);
                
                if (this.state.roundOver()) {
                    break;
                } else if (this.state.turnOver()) {
                    this.state.endTurn();
                }
                turnsPlayed++;
            }
            
            this.state.updateWinnerPoints();

            System.out.println("---------------------------");
            System.out.println("AI #" + this.state.getCurrentPlayer().getId() + " won this round!");
            System.out.println("They got " + this.state.calculateRoundPoints() + " points from this round and have a total of" + this.state.getCurrentPlayer().getPoints() + " points!");
            System.out.println("The loser got 0 points from this round and has a total of " + this.state.getWaitingPlayer().getPoints() + " points!");
            System.out.println("Turns played: " + turnsPlayed);
            roundCount++;
            
            if (this.state.gameOver()) {
                break;
            }
            
            this.state = this.state.startNewRound();
        }
        
        System.out.println("Game over!");
        System.out.println("AI #" + this.state.getCurrentPlayer().getId() + " won the game by reaching 100 points first");
        System.out.println("Rounds played: " + roundCount);
        System.out.println("---------------------------");
        System.out.println("----------RESULTS----------");
        System.out.println("AI #" + this.state.getCurrentPlayer().getId() + " got " + this.state.getCurrentPlayer().getPoints() + " points");
        System.out.println("AI #" + this.state.getWaitingPlayer().getId() + " got " + this.state.getWaitingPlayer().getPoints() + " points");
        System.out.println("---------------------------");
    }
}
