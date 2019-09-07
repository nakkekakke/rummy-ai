package rummy.game.ui;

import java.util.List;
import java.util.Scanner;
import rummy.ai.ISMCTS;
import rummy.game.domain.Card;
import rummy.game.domain.Layoff;
import rummy.game.domain.State;
import rummy.game.domain.meld.Meld;
import rummy.game.domain.move.DrawMove;
import rummy.game.domain.move.Move;

/**
 * Runs the AI vs human game.
 */
public class AIvsPlayer_UI {
    private State state;
    private final Scanner scanner;
    
    public AIvsPlayer_UI(State state, Scanner scanner) {
        this.state = state;
        this.scanner = scanner;
    }
    
    public void play() { // Compared to an AI vs AI game, here the AI can have a lot more thinking time because the human opponent will also think for a long time every turn
        System.out.println("AI vs human game starting!");
        System.out.println("Enter the AI level for your opponent");
        System.out.println("Level 1 = very easy, 2500 iterations per move");
        System.out.println("Level 2 = easy, 10000 iterations per move");
        System.out.println("Level 3 = medium, 25000 iterations per move");
        System.out.println("Level 4 = hard, 50000 iterations per move");
        System.out.println("Level 5 = god, 100000 iterations per move (might be laggy)");
        
        int aiTime = Integer.parseInt(this.scanner.nextLine());
        
        if (aiTime == 1) {
            aiTime = 2500;
        } else if (aiTime == 2) {
            aiTime = 10000;
        } else if (aiTime == 3) {
            aiTime = 25000;
        } else if (aiTime == 4) {
            aiTime = 50000;
        } else {
            aiTime = 100000;
        }
        
        System.out.println("Do you want to go first? (y/n)");
        int aiPlayer;
        String firstAnswer = this.scanner.nextLine();
        if (firstAnswer.equalsIgnoreCase("y")) {
            aiPlayer = this.state.getWaitingPlayer().getId();
        } else {
            aiPlayer = this.state.getCurrentPlayer().getId();
        }
        
        System.out.println("New game starting, good luck!");
        int roundCount = 0;
        while (true) {
            System.out.println("Starting round " + (roundCount + 1) + "!");
            
            while (!this.state.roundOver()) {
                if (this.state.getCurrentPlayer().getId() == aiPlayer) {
                    System.out.println("It's AI's turn");
                    playAiTurn(aiTime);
                } else {
                    System.out.println("It's your turn");
                    playPlayerTurn();
                }
                System.out.println("End of turn");
                System.out.println("");
                System.out.println("---------------------------");
                System.out.println("");
            }
            
            this.state.updateWinnerPoints();
            printRoundEnd(aiPlayer);

            roundCount++;
            
            if (this.state.gameOver()) {
                break;
            }
            
            this.state = this.state.startNewRound();
        }
        
        printGameOver(aiPlayer);
        System.out.println("Rounds played: " + roundCount);
    }
    
    private void playAiTurn(int aiTime) {
        while (!this.state.getAvailableMoves().isEmpty()) {
            ISMCTS ai = new ISMCTS(this.state, aiTime);
            Move nextMove = ai.run();
            
            if (!nextMove.type().equals("pass")) {
                System.out.println("AI's next move is: " + nextMove);
            }
            
            if (nextMove.type().equals("draw")) {
                DrawMove drawMove = (DrawMove) nextMove;
                if (!drawMove.isDeckDraw()) {
                    System.out.println("AI drew " + this.state.getDiscardPileTop() + " from the discard pile");
                }
            }
            
            this.state.doMove(nextMove, false);

            if (this.state.roundOver()) {
                break;
            } else if (this.state.turnOver()) {
                this.state.endTurn();
                break;
            }
        }
    }
    
    private void playPlayerTurn() {
        printPlayerInfo();
        while (true) {
            String phase = this.state.getPhase();
            switch (phase) {
                case "draw":
                    playerDraw();
                    break;
                case "layoff":
                    playerLayoff();
                    break;
                case "meld":
                    playerMeld();
                    break;
                case "discard":
                    playerDiscard();
                    break;
                default:
                    break;
            }
            
            if (this.state.roundOver()) {
                break;
            } else if (this.state.turnOver()) {
                this.state.endTurn();
                break;
            }
        }
    }
    
    private void playerDraw() {
        System.out.println("Draw from deck (1) or discard pile (2)?");
        String drawChoice = this.scanner.nextLine();
        Card draw;
        if (drawChoice.equals("1")) {
            draw = this.state.drawFromDeck();
        } else {
            draw = this.state.drawFromDiscardPile();
        }

        System.out.println("Drew " + draw);

        this.state.organizeCurrentHand();
        System.out.println("");
    }
    
    private void playerMeld() {
        System.out.println("Checking for possible melds in your hand");
        List<Meld> melds = this.state.findPossibleMelds();
        if (!melds.isEmpty()) {
            for (int i = 0; i < melds.size(); i++) {
                System.out.println((i + 1) + ": " + melds.get(i).getCards());
                System.out.println("");
            }

            if (melds.size() == 1) {
                System.out.println("");
                System.out.println("Select 1 to meld or 0 to not meld");
            } else {
                System.out.println("Select which option to meld (1-" + melds.size() + ") or select 0 to not meld anything");
            }

            int meldChoice = Integer.parseInt(this.scanner.nextLine());

            if (meldChoice != 0) {
                Meld meld = this.state.meld(melds.get(meldChoice - 1));
                System.out.println("Melded: " + meld.getCards());
            }

        } else {
            System.out.println("No possible melds in your hand");
        }
        System.out.println("");
        this.state.setPhase("layoff");
    }
    
    private void playerLayoff() {
        boolean searchForLayoffs = true;

        while (searchForLayoffs) {
            searchForLayoffs = false;

            System.out.println("Checking for possible layoffs");
            List<Layoff> layoffs = this.state.findPossibleLayoffs();

            if (!layoffs.isEmpty()) {

                if (layoffs.size() == 1) {
                    System.out.println("Select 1 to lay off or 0 to not lay off");
                } else {
                    System.out.println("Select option for layoff (1-" + layoffs.size() + " or select 0 to not lay off anything");
                }

                for (int i = 0; i < layoffs.size(); i++) {
                    System.out.println((i+1) + ": " + layoffs.get(i).getCard() + " into meld " + layoffs.get(i).getMeld().getCards());
                }

                int layoffChoice = Integer.parseInt(this.scanner.nextLine());
                    if (layoffChoice != 0) {
                        this.state.layoff(layoffs.get(layoffChoice - 1), false);
                        System.out.println("Laid " + layoffs.get(layoffChoice - 1).getCard() + " into meld " + layoffs.get(layoffChoice - 1).getMeld().getCards());
                        searchForLayoffs = true;
                    } else {
                        System.out.println("Skipping this layoff");
                    }

            } else {
                System.out.println("No possible layoffs");
                break;
            }
        }
        System.out.println("");
        this.state.setPhase("discard");
    }
    
    private void playerDiscard() {
        System.out.println("Select which card to discard (1-" + this.state.getCurrentPlayer().getHand().size() + ")");
        for (int i = 0; i < this.state.getCurrentPlayer().getHand().size(); i++) {
            System.out.println((i + 1) + ": " + this.state.getCurrentPlayer().getHand().get(i));
        }
        String discardChoice = this.scanner.nextLine();
        Card discarded = this.state.discardCardNumber(Integer.parseInt(discardChoice) - 1);
        System.out.println(discarded + " discarded");

        System.out.println("");
        this.state.setPhase("end");
    }
    
    private void printPlayerInfo() {
        System.out.println("Deck has " + this.state.getDeckSize() + " cards");
        System.out.println("Opponent has " + this.state.getOpponentHandSize() + " cards");

        List<Meld> currentMelds = this.state.getMelds();

        if (!currentMelds.isEmpty()) {
            System.out.println("Current melds on board are:");
            for (Meld meld : currentMelds) {
                System.out.println(meld.getCards());
            }
        }

        System.out.println("");

        System.out.println("Your hand:");
        System.out.println(this.state.getCurrentPlayer().getHand());
        System.out.println("");

        System.out.println("Top card of the discard pile is " + this.state.getDiscardPileTop());
        System.out.println("");
    }
    
    private void printRoundEnd(int aiPlayer) {
        System.out.println("---------------------------");
        System.out.println("---------------------------");
        System.out.println("");
        if (this.state.getCurrentPlayer().getId() == aiPlayer) {
            System.out.println("AI won this round!");
            System.out.println("They got " + this.state.calculateRoundPoints() + " points from this round and have a total of " + this.state.getCurrentPlayer().getPoints() + " points!");
            System.out.println("You got 0 points from this round and have a total of " + this.state.getWaitingPlayer().getPoints() + " points!");
        } else {
            System.out.println("You won this round!");
            System.out.println("You got " + this.state.calculateRoundPoints() + " points from this round and have a total of " + this.state.getCurrentPlayer().getPoints() + " points!");
            System.out.println("The AI got 0 points from this round and has a total of " + this.state.getWaitingPlayer().getPoints() + " points!");
        }
        System.out.println("");
        System.out.println("---------------------------");
        System.out.println("---------------------------");
        System.out.println("");
    }
    
    private void printGameOver(int aiPlayer) {
        System.out.println("");
        System.out.println("---------------------------");
        System.out.println("---------------------------");
        System.out.println("");
        
        System.out.println("Game over!");
        
        if (this.state.getCurrentPlayer().getId() == aiPlayer) {
            System.out.println("AI won the game by reaching 100 points first");
            System.out.println("---------------------------");
            System.out.println("----------RESULTS----------");
            System.out.println("AI got " + this.state.getCurrentPlayer().getPoints() + " points");
            System.out.println("You got " + this.state.getWaitingPlayer().getPoints() + " points");
            System.out.println("---------------------------");
        } else {
            System.out.println("You won the game by reaching 100 points first");
            System.out.println("---------------------------");
            System.out.println("----------RESULTS----------");
            System.out.println("You got " + this.state.getCurrentPlayer().getPoints() + " points");
            System.out.println("AI got " + this.state.getWaitingPlayer().getPoints() + " points");
            System.out.println("---------------------------");
        }
    }
}
