package rummy;

import java.util.List;
import java.util.Scanner;


public class PlayGame {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Logger log; 
        
        System.out.println("Type y to start with debug logger");
        String debug = scanner.nextLine();
        if (debug.equals("y")) {
            log = new Logger(true);
        } else {
            log = new Logger(false);
        }
        
        Game game;
        System.out.println("Which player goes first? Type 1 or 2");
        String first = scanner.nextLine();
        if (first.equals("1")) {
            game = new Game(1, log);
        } else {
            game = new Game(2, log);
        }
        
        game.initialDraw();
        System.out.println("");
        System.out.println("");
        
        // GAME LOOP
        while (true) {
            int currentPlayer = game.getCurrentPlayerId();
            System.out.println("");
            System.out.println("-------------------------------");
            System.out.println("");
            System.out.println("It's player " + currentPlayer + "'s turn");
            System.out.println("Deck has " + game.getDeckSize() + " cards");
            System.out.println("Opponent has " + game.getHandSize(false) + " cards");
            
            List<Meld> currentMelds = game.getCurrentMelds();
            
            if (!currentMelds.isEmpty()) {
                System.out.println("Current melds on board are:");
                for (Meld meld : currentMelds) {
                    System.out.println(meld.getCards());
                }
            }
            
            
            System.out.println("");
            
            System.out.println("Your hand:");
            System.out.println(game.getPlayerHand());
            System.out.println("");
            
            System.out.println("Top card of the discard pile is " + game.getDiscardPileTop());
            System.out.println("");
            System.out.println("Draw from deck (1) or discard pile (2)?");
            String drawChoice = scanner.nextLine();
            Card draw;
            if (drawChoice.equals("1")) {
                draw = game.drawFromDeck();
            } else {
                draw = game.drawFromDiscardPile();
            }
            
            System.out.println("Drew " + draw);
            
            game.organizeCurrentHand();
            
            System.out.println("");
            System.out.println("Checking for possible melds in your hand");
            List<Meld> melds = game.findPossibleMelds();
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
                    Meld meld = game.meld(melds.get(meldChoice - 1));
                    System.out.println("Melded: " + meld.getCards());
                }
                
            } else {
                System.out.println("No possible melds in your hand");
            }
            
            while (true) {
                System.out.println("Checking for possible layoffs");
                List<Layoff> layoffs = game.findPossibleLayoffs();
                if (!layoffs.isEmpty()) {
                    for (int i = 0; i < layoffs.size(); i++) {
                        System.out.println((i + 1) + ": " + layoffs.get(i).getCard() + " into meld " + layoffs.get(i).getMeld().getCards());
                    }

                    if (layoffs.size() == 1) {
                        System.out.println("Select 1 to layoff or 0 to not layoff");
                    } else {
                        System.out.println("Select layoff option (1-" + layoffs.size() + ") or select 0 to not layoff anything");
                    }

                    int layoffChoice = Integer.parseInt(scanner.nextLine());

                    if (layoffChoice != 0) {
                        Layoff layoff = game.layoff(layoffs.get(layoffChoice - 1));
                        System.out.println("Laid " + layoff.getCard() + " into meld " + layoff.getMeld().getCards());
                    } else {
                        System.out.println("Skipping layoffs");
                    }
                } else {
                    System.out.println("No possible layoffs");
                    break;
                }
            }
            
            System.out.println("");
            System.out.println("Select which card to discard (1-" + game.getHandSize(true) + ")");
            System.out.println(game.getPlayerHand());
            String discardChoice = scanner.nextLine();
            Card discarded = game.discardCardNumber(Integer.parseInt(discardChoice) - 1);
            System.out.println(discarded + " discarded");
            
            System.out.println("");
            
            if (game.gameOver()) {
                break;
            }
            
            System.out.println("End of turn");

            game.endTurn();
        }
        
        
        
        
        
        System.out.println("Player " + game.getState().getCurrentPlayer().getId() + " won!");
    }
}
