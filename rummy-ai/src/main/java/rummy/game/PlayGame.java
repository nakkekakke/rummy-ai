package rummy.game;

import rummy.game.domain.meld.Meld;
import java.util.List;
import java.util.Scanner;
import rummy.game.domain.Card;
import rummy.game.domain.Layoff;
import rummy.game.domain.Player;
import rummy.game.domain.State;


public class PlayGame {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
//        Logger log; 
//        
//        System.out.println("Type y to start with debug logger");
//        String debug = scanner.nextLine();
//        if (debug.equals("y")) {
//            log = new Logger(true);
//        } else {
//            log = new Logger(false);
//        }
        
        State state;
        System.out.println("Which player goes first? Type 1 or 2");
        String first = scanner.nextLine();
        if (first.equals("1")) {
            state = new State(1);
        } else {
            state = new State(2);
        }
        
        while (true) {
            System.out.println("------------------");
            System.out.println("");
            System.out.println("New round started!");
            System.out.println("");
            System.out.println("------------------");

            // GAME LOOP
            while (true) {
                Player currentPlayer = state.getCurrentPlayer();
                System.out.println("");
                System.out.println("-------------------------------");
                System.out.println("");
                System.out.println("It's player " + currentPlayer.getId() + "'s turn");
                System.out.println("Deck has " + state.getDeckSize() + " cards");
                System.out.println("Opponent has " + state.getOpponentHandSize() + " cards");

                List<Meld> currentMelds = state.getMelds();

                if (!currentMelds.isEmpty()) {
                    System.out.println("Current melds on board are:");
                    for (Meld meld : currentMelds) {
                        System.out.println(meld.getCards());
                    }
                }


                System.out.println("");

                System.out.println("Your hand:");
                System.out.println(currentPlayer.getHand());
                System.out.println("");

                System.out.println("Top card of the discard pile is " + state.getDiscardPileTop());
                System.out.println("");
                System.out.println("Draw from deck (1) or discard pile (2)?");
                String drawChoice = scanner.nextLine();
                Card draw;
                if (drawChoice.equals("1")) {
                    draw = state.drawFromDeck();
                } else {
                    draw = state.drawFromDiscardPile();
                }

                System.out.println("Drew " + draw);

                state.organizeCurrentHand();

                System.out.println("");
                System.out.println("Checking for possible melds in your hand");
                List<Meld> melds = state.findPossibleMelds();
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

                    int meldChoice = Integer.parseInt(scanner.nextLine());

                    if (meldChoice != 0) {
                        Meld meld = state.meld(melds.get(meldChoice - 1));
                        System.out.println("Melded: " + meld.getCards());
                    }

                } else {
                    System.out.println("No possible melds in your hand");
                }


                boolean searchForLayoffs = true;

                while (searchForLayoffs) {
                    searchForLayoffs = false;

                    System.out.println("Checking for possible layoffs");
                    List<Layoff> layoffs = state.findPossibleLayoffs();

                    if (!layoffs.isEmpty()) {

                        if (layoffs.size() == 1) {
                            System.out.println("Select 1 to lay off or 0 to not lay off");
                        } else {
                            System.out.println("Select option for layoff (1-" + layoffs.size() + " or select 0 to not lay off anything");
                        }

                        for (int i = 0; i < layoffs.size(); i++) {
                            System.out.println((i+1) + ": " + layoffs.get(i).getCard() + " into meld " + layoffs.get(i).getMeld().getCards());
                        }

                        int layoffChoice = Integer.parseInt(scanner.nextLine());
                            if (layoffChoice != 0) {
                                state.layoff(layoffs.get(layoffChoice - 1));
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

                if (state.roundOver()) {
                    break;
                }

                System.out.println("");
                System.out.println("Select which card to discard (1-" + currentPlayer.getHand().size() + ")");
                System.out.println(currentPlayer.getHand());
                String discardChoice = scanner.nextLine();
                Card discarded = state.discardCardNumber(Integer.parseInt(discardChoice) - 1);
                System.out.println(discarded + " discarded");

                System.out.println("");

                if (state.roundOver()) {
                    break;
                }

                System.out.println("End of turn");

                state.endTurn();
            }
            
            System.out.println("Player " + state.getCurrentPlayer().getId() + " won this hand");
            System.out.println("Points awarded: " + state.calculateRoundPoints());
            
            state.updateWinnerPoints();
            
            System.out.println("");
            System.out.println("Player " + state.getCurrentPlayer().getId() + " has " + state.getCurrentPlayer().getPoints() + " points");
            System.out.println("Player " + state.getWaitingPlayer().getId() + " has " + state.getWaitingPlayer().getPoints() + " points");
            System.out.println("");

            if (state.gameOver()) {
                break;
            }
            
            state = state.startNewRound();
        }
        
        System.out.println("Player " + state.getCurrentPlayer().getId() + " reached 100 points and won the game!");
    }
}
