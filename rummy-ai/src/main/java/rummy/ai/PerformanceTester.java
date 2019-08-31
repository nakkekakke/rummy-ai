package rummy.ai;

import java.util.ArrayList;
import java.util.LongSummaryStatistics;
import java.util.Scanner;
import rummy.game.domain.State;
import rummy.game.domain.move.Move;


public class PerformanceTester {
    private State state;
    private final int startingPlayer;
    private final Scanner scanner;
    
    public PerformanceTester(State state, Scanner scanner) {
        this.state = state;
        this.startingPlayer = state.getCurrentPlayer().getId();
        this.scanner = scanner;
    }
    
    // Lazily copy pasted from AIvsAI_UI class, with added performance tracking. Could refactor in the future.
    public void run() {
        System.out.println("Performance tests starting!");
        System.out.println("This test supports three different AI levels");
        System.out.println("Level 1 = 250 iterations per move");
        System.out.println("Level 2 = 1000 iterations per move");
        System.out.println("Level 3 = 5000 iterations per move");
        System.out.println("Enter AI #1's level (1-3): ");
        int aiOneTime = Integer.parseInt(this.scanner.nextLine());
        if (aiOneTime == 1) {
            aiOneTime = 250;
        } else if (aiOneTime == 2) {
            aiOneTime = 1000;
        } else {
            aiOneTime = 5000;
        }
        
        System.out.println("Enter AI #2's level (1-3): ");
        int aiTwoTime = Integer.parseInt(this.scanner.nextLine());
        if (aiTwoTime == 1) {
            aiTwoTime = 250;
        } else if (aiTwoTime == 2) {
            aiTwoTime = 1000;
        } else {
            aiTwoTime = 5000;
        }
        
        System.out.println("How many games to play?");
        System.out.println("One game takes usually 1-6 minutes, heavily depending on the AI levels so don't select a number too high!");
        int gamesToPlay = Integer.parseInt(this.scanner.nextLine());
        
        // Performance trackers
        ArrayList<Long> player1MoveTimes = new ArrayList<>();
        ArrayList<Long> player2MoveTimes = new ArrayList<>();
        ArrayList<Long> roundTimes = new ArrayList<>();
        ArrayList<Long> gameTimes = new ArrayList<>();
        int player1Scores = 0;
        int player2Scores = 0;
        int player1RoundWins = 0;
        int player1GameWins = 0;
        int player2RoundWins = 0;
        int player2GameWins = 0;
        
        
        for (int i = 0; i < gamesToPlay; i++) {
            long gameStart = System.currentTimeMillis();
            this.state = new State(startingPlayer);
            //int roundCount = 0;

            OUTER:
            while (true) {
                long start = System.currentTimeMillis();
                //System.out.println("Starting a new round!");


                //int turnsPlayed = 0;

                while (!this.state.getAvailableMoves().isEmpty()) {
                    //System.out.println(this.state);
                    ISMCTS aiOne = new ISMCTS(this.state, aiOneTime);
                    ISMCTS aiTwo = new ISMCTS(this.state, aiTwoTime);
                    Move nextMove;

                    if (this.state.getCurrentPlayer().getId() == 1) {
                        aiOne.setRootState(this.state);
                        long startP1 = System.currentTimeMillis();
                        nextMove = aiOne.run();
                        player1MoveTimes.add(System.currentTimeMillis() - startP1);
                    } else {
                        aiTwo.setRootState(this.state);
                        long startP2 = System.currentTimeMillis();
                        nextMove = aiTwo.run();
                        player2MoveTimes.add(System.currentTimeMillis() - startP2);
                    }

                    //System.out.println("Next move " + nextMove);
                    this.state.doMove(nextMove, false);

                    if (this.state.roundOver()) {
                        break;
                    } else if (this.state.turnOver()) {
                        this.state.endTurn();
                    }
                    //turnsPlayed++;
                    //if (turnsPlayed > 1000) {
                        //System.out.println("Something went wrong, please try again");
                        //break OUTER;
                    //}
                }
                roundTimes.add(System.currentTimeMillis() - start);

                this.state.updateWinnerPoints();

                //System.out.println("---------------------------");
                System.out.println("AI #" + this.state.getCurrentPlayer().getId() + " won this round!");
                if (this.state.getCurrentPlayer().getId() == 1) {
                    player1RoundWins++;
                    player1Scores += this.state.calculateRoundPoints();
                } else {
                    player2RoundWins++;
                    player2Scores += this.state.calculateRoundPoints();
                }
                //System.out.println("They got " + this.state.calculateRoundPoints() + " points from this round and have a total of" + this.state.getCurrentPlayer().getPoints() + " points!");
                //System.out.println("The loser got 0 points from this round and has a total of " + this.state.getWaitingPlayer().getPoints() + " points!");
                //System.out.println("Turns played: " + turnsPlayed);
                //roundCount++;

                if (this.state.gameOver()) {
                    break;
                }

                this.state = this.state.startNewRound();
            }

            gameTimes.add((System.currentTimeMillis() - gameStart) / 1000);

            //System.out.println("Game over!");
            System.out.println("AI #" + this.state.getCurrentPlayer().getId() + " won the game by reaching 100 points first");
            if (this.state.getCurrentPlayer().getId() == 1) {
                player1GameWins++;
            } else {
                player2GameWins++;
            }
            //System.out.println("Rounds played: " + roundCount);
            //System.out.println("---------------------------");
            //System.out.println("----------RESULTS----------");
            System.out.println("AI #" + this.state.getCurrentPlayer().getId() + " got " + this.state.getCurrentPlayer().getPoints() + " points");
            System.out.println("AI #" + this.state.getWaitingPlayer().getId() + " got " + this.state.getWaitingPlayer().getPoints() + " points");
            //System.out.println("---------------------------");
        }
        
        double p1MoveAverage = player1MoveTimes.stream().mapToLong(Long::longValue).average().orElse(-1.0);
        double p2MoveAverage = player2MoveTimes.stream().mapToLong(Long::longValue).average().orElse(-1.0);
        LongSummaryStatistics roundStats = roundTimes.stream().mapToLong(Long::longValue).summaryStatistics();
        
        System.out.println();
        System.out.println("---------------------------");
        System.out.println("PERFORMANCE TEST RESULTS");
        System.out.println("---------------------------");
        System.out.println("Average times used for a single move:");
        System.out.println("Player 1: " + p1MoveAverage + " ms");
        System.out.println("Player 2: " + p2MoveAverage + " ms");
        System.out.println("Average round time: " + roundStats.getAverage() + " ms");
        System.out.println("Total rounds: " + roundStats.getCount());
        System.out.println("Game times in seconds: " + gameTimes);
        System.out.println("Average game length: " + gameTimes.stream().mapToLong(Long::longValue).average().orElse(-1.0));
        System.out.println("Round wins for player 1: " + player1RoundWins);
        System.out.println("Round wins for player 2: " + player2RoundWins);
        System.out.println("Game wins for player 1: " + player1GameWins);
        System.out.println("Game wins for player 2: " + player2GameWins);
        System.out.println("Player 1 total score: " + player1Scores);
        System.out.println("Player 2 total score: " + player2Scores);
    }
    
    
}
